package edument.perl6idea.refactoring;

import com.intellij.lang.ContextAwareActionHandler;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class Perl6ExtractCodeBlockHandler implements RefactoringActionHandler, ContextAwareActionHandler {
    public static final String TITLE = "Code Block Extraction";
    private final Perl6CodeBlockType myCodeBlockType;
    private Perl6StatementList myParentElement;
    private PsiElement[] myCodeBlockContent;

    public Perl6ExtractCodeBlockHandler(Perl6CodeBlockType type) {
        myCodeBlockType = type;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        final Pass<PsiElement[]> callback = new Pass<PsiElement[]>() {
            public void pass(final PsiElement[] selectedValue) {
                invokeOnElements(project, editor, file, selectedValue);
            }
        };
        selectAndPass(project, editor, file, callback);
    }

    private static void selectAndPass(Project project, Editor editor, PsiFile file, Pass<PsiElement[]> callback) {
        editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
        if (!editor.getSelectionModel().hasSelection()) {
            final int offset = editor.getCaretModel().getOffset();
            final List<Perl6PsiElement> expressions = collectExpressions(file, offset);
            PsiDocumentManager.getInstance(project).commitAllDocuments();
            if (expressions.isEmpty()) {
                // TODO Throw an exception here
            } else if (expressions.size() == 1) {
                callback.pass(new PsiElement[]{expressions.get(0)});
                return;
            } else {
                IntroduceTargetChooser.showChooser(editor, expressions, new Pass<Perl6PsiElement>() {
                    @Override
                    public void pass(Perl6PsiElement perl6PsiElement) {
                        callback.pass(new PsiElement[]{perl6PsiElement});
                    }
                }, psi -> psi.getText());
                return;
            }
        }
        callback.pass(getElements(project, editor, file));
    }

    private static PsiElement[] getElements(Project project, Editor editor, PsiFile file) {
        SelectionModel selectionModel = editor.getSelectionModel();
        PsiElement startLeaf = file.findElementAt(selectionModel.getSelectionStart());
        PsiElement endLeaf = file.findElementAt(selectionModel.getSelectionEnd());
        PsiElement start = PsiTreeUtil.getNonStrictParentOfType(skipSpaces(startLeaf, true), Perl6Statement.class);
        PsiElement end = PsiTreeUtil.getNonStrictParentOfType(skipSpaces(endLeaf, false), Perl6Statement.class);

        if (start == null || end == null) {
            if (end != null)
                return new PsiElement[]{end};
            else if (start != null)
                return new PsiElement[]{start};
            else {
                return PsiElement.EMPTY_ARRAY;
            }
        }

        if (PsiTreeUtil.isAncestor(start, end, true)) {
            return new PsiElement[]{start};
        } else if (PsiTreeUtil.isAncestor(end, start, true)) {
            return new PsiElement[]{end};
        }

        List<PsiElement> elements = new ArrayList<>();
        while (start != end) {
            elements.add(start);
            start = start.getNextSibling();
        }
        elements.add(end);
        return elements.toArray(PsiElement.EMPTY_ARRAY);
    }

    private static PsiElement skipSpaces(PsiElement node, boolean toRight) {
        PsiElement temp = node;
        while (temp != null && (temp instanceof PsiWhiteSpace || temp.getNode().getElementType().equals(UNV_WHITE_SPACE)))
            temp = toRight ? temp.getNextSibling() : temp.getPrevSibling();
        return temp;
    }

    @NotNull
    private static List<Perl6PsiElement> collectExpressions(PsiFile file, int offset) {
        List<Perl6PsiElement> exprs = new ArrayList<>();
        PsiElement element = file.findElementAt(offset);
        while (!(element instanceof Perl6StatementList) && element != null) {
            if (element instanceof P6Extractable && element instanceof Perl6PsiElement)
                exprs.add((Perl6PsiElement)element);
            element = element.getParent();
        }
        return exprs;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        if (dataContext != null) {
            final PsiFile file = CommonDataKeys.PSI_FILE.getData(dataContext);
            final Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
            if (file != null && editor != null) {
                invokeOnElements(project, editor, file, elements);
            }
        }
    }

    private void invokeOnElements(Project project, Editor editor, PsiFile file, PsiElement[] elements) {
        // Firstly, we need to know what element will be a holder for new code block
        PsiElement commonParent = PsiTreeUtil.findCommonParent(elements);
        Perl6StatementList list = PsiTreeUtil.getNonStrictParentOfType(commonParent, Perl6StatementList.class);
        List<Perl6StatementList> scopes = new ArrayList<>();
        while (list != null) {
            scopes.add(list);
            list = PsiTreeUtil.getParentOfType(list, Perl6StatementList.class);
        }

        IntroduceTargetChooser.showChooser(editor, scopes, new Pass<Perl6StatementList>() {
            @Override
            public void pass(Perl6StatementList list) {
                getDataAndCreateMethod(project, editor, list, elements);
            }
        }, psi -> psi.getText(), "Creation scope");
    }

    private void getDataAndCreateMethod(Project project,
                                        Editor editor,
                                        Perl6StatementList list,
                                        PsiElement[] elements) {
        myParentElement = list;
        myCodeBlockContent = elements;
        TransactionGuard.getInstance().submitTransactionAndWait(() -> {
            Perl6ExtractMethodDialog dialog = new Perl6ExtractMethodDialog(project, TITLE, myCodeBlockType) {
                @Override
                protected void doAction() {
                        executeMethodCreation(project, editor, getName());
                }
            };
            dialog.show();
        });
    }

    private void executeMethodCreation(Project project, Editor editor, String name) {
        Perl6StatementList parent = myParentElement;
        PsiElement beforeAnchor = calculateBeforeAnchor(parent, editor);
        if (beforeAnchor == null) return;

        List<String> contents = new ArrayList<>();
        for (PsiElement line : myCodeBlockContent) {
            if (line instanceof Perl6Statement) {
                contents.add(line.getText());
            }
        }
        PsiElement newBlock = Objects.equals(myCodeBlockType, Perl6CodeBlockType.ROUTINE) ?
                              Perl6ElementFactory.createRoutine(editor.getProject(), name, new ArrayList<>(), contents) :
                              Perl6ElementFactory.createMethod(editor.getProject(), name, new ArrayList<>(), contents);
        Perl6StatementList statements = PsiTreeUtil.findChildOfType(newBlock, Perl6StatementList.class);
        if (statements == null) {
            reportError(editor);
            return;
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            parent.getNode().addChild(newBlock.getNode(), beforeAnchor.getNode());
            for (PsiElement el : myCodeBlockContent) {
                el.delete();
            }
        });
    }

    private PsiElement calculateBeforeAnchor(Perl6StatementList parent, Editor editor) {
        PsiElement commonParent = PsiTreeUtil.findCommonParent(myCodeBlockContent);
        if (commonParent == null) return reportError(editor);

        // If created in the same block, before first element will be an anchor already
        if (parent == commonParent) return myCodeBlockContent[0];

        // TODO
        return myCodeBlockContent[0];
    }

    @Nullable
    private static PsiElement reportError(Editor editor) {
        CommonRefactoringUtil.showErrorHint(editor.getProject(), editor, "Cannot extract this code", TITLE,
                                            "refactoring.extractMethod");
        return null;
    }

    @Override
    public boolean isAvailableForQuickList(@NotNull Editor editor, @NotNull PsiFile file, @NotNull DataContext dataContext) {
        return false;
    }
}
