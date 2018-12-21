package edument.perl6idea.refactoring;

import com.intellij.lang.ContextAwareActionHandler;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Perl6ExtractCodeBlockHandler implements RefactoringActionHandler, ContextAwareActionHandler {
    public static final String TITLE = "Code Block Extraction";
    private final Perl6CodeBlockType myCodeBlockType;

    public Perl6ExtractCodeBlockHandler(Perl6CodeBlockType type) {
        myCodeBlockType = type;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        if (dataContext != null) {
            final Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
            final PsiFile file = CommonDataKeys.PSI_FILE.getData(dataContext);
            if (file != null && editor != null) {
                invoke(project, editor, file, elements);
            }
        }
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        invoke(project, editor, file, getStatementsToExtract(file, editor));
    }

    protected void invoke(@NotNull Project project, Editor editor, PsiFile file, PsiElement[] elements) {
        // Gets a parent scope for new block according to callback-based API
        List<Perl6StatementList> scopes = getPossibleScopes(elements);
        IntroduceTargetChooser.showChooser(editor, scopes, new Pass<Perl6StatementList>() {
            @Override
            public void pass(Perl6StatementList list) {
                invoke(project, editor, file, list, elements);
            }
        }, PsiElement::getText, "Select creation scope");
    }

    protected void invoke(@NotNull Project project, Editor editor, PsiFile file, PsiElement parentScope, PsiElement[] elements) {
        if (checkElementsSanity(project, editor, parentScope, elements)) return;
        if (checkMethodSanity(project, editor, parentScope)) return;

        /* Anchor represents an element, that will be a next to created one or null,
         * if no such anchor is possible, e.g. when a method is added after last one in a class */
        PsiElement anchor = getAnchor(parentScope, elements);

        NewCodeBlockData newCodeBlockData = getNewBlockData(project);
        // If user cancelled action or exception occurred
        if (newCodeBlockData == null) return;
        List<String> contents = Arrays.stream(elements).map(p -> p.getText()).collect(Collectors.toList());
        PsiElement newBlock = createNewBlock(project, newCodeBlockData, contents);
        insertNewCodeBlock(project, parentScope, newBlock, anchor);
        replaceStatementsWithCall(project, newCodeBlockData, parentScope, elements);
    }

    private boolean checkElementsSanity(@NotNull Project project, Editor editor, PsiElement parentScope, PsiElement[] elements) {
        if (elements.length == 0 || parentScope == null) {
            reportError(project, editor, "Cannot extract code");
            return true;
        }
        return false;
    }

    private boolean checkMethodSanity(@NotNull Project project, Editor editor, PsiElement parentScope) {
        // Check if method outside of class creation
        if (myCodeBlockType == Perl6CodeBlockType.METHOD || myCodeBlockType == Perl6CodeBlockType.PRIVATEMETHOD) {
            PsiElement wrapper = PsiTreeUtil.getParentOfType(parentScope,
                    Perl6File.class, Perl6RoutineDecl.class, Perl6PackageDecl.class);
            if (wrapper instanceof Perl6PackageDecl) {
                String kind = ((Perl6PackageDecl) wrapper).getPackageKind();
                if (kind == null || !kind.equals("class") && !kind.equals("role") &&
                    !kind.equals("grammar") && !kind.equals("monitor")) {
                    reportError(project, editor, "Cannot extract a method into " + kind);
                    return true;
                }
            } else if (wrapper instanceof Perl6RoutineDecl) {
                String kind = ((Perl6RoutineDecl)wrapper).getRoutineKind();
                if (kind == null || !kind.equals("method")) {
                    reportError(project, editor, "Cannot extract a method into " + kind);
                    return true;
                }
            } else {
                reportError(project, editor, "Cannot extract a method outside of class, monitor, grammar or role");
                return true;
            }

        }
        return false;
    }

    protected static PsiElement[] getStatementsToExtract(PsiFile file, Editor editor) {
        if (editor.getSelectionModel().hasSelection()) {
            return getElementsFromSelection(file, editor);
        } else {
            return getElementsFromCaret(file, editor);
        }
    }

    private static PsiElement[] getElementsFromSelection(PsiFile file, Editor editor) {
        SelectionModel selectionModel = editor.getSelectionModel();
        PsiElement startLeaf = file.findElementAt(selectionModel.getSelectionStart());
        PsiElement endLeaf = file.findElementAt(selectionModel.getSelectionEnd());
        PsiElement start = PsiTreeUtil.getNonStrictParentOfType(Perl6PsiUtil.skipSpaces(startLeaf, true), Perl6Statement.class);
        PsiElement end = PsiTreeUtil.getNonStrictParentOfType(Perl6PsiUtil.skipSpaces(endLeaf, false), Perl6Statement.class);

        if (start == null || end == null) {
            if (end != null) {
                return new PsiElement[]{end};
            }
            else if (start != null) {
                return new PsiElement[]{start};
            }
            else {
                return PsiElement.EMPTY_ARRAY;
            }
        }

        if (PsiTreeUtil.isAncestor(start, end, true)) {
            return new PsiElement[]{start};
        }
        else if (PsiTreeUtil.isAncestor(end, start, true)) {
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

    private static PsiElement[] getElementsFromCaret(PsiFile file, Editor editor) {
        int offset = editor.getCaretModel().getOffset();
        List<PsiElement> exprs = new ArrayList<>();
        PsiElement element = file.findElementAt(offset);
        while (element != null && !(element instanceof Perl6StatementList)) {
            if (element instanceof P6Extractable)
                exprs.add(element);
            element = element.getParent();
        }
        return exprs.toArray(PsiElement.EMPTY_ARRAY);
    }

    protected List<Perl6StatementList> getPossibleScopes(PsiElement[] elements) {
        PsiElement commonParent = PsiTreeUtil.findCommonParent(elements);
        Perl6StatementList list = PsiTreeUtil.getNonStrictParentOfType(commonParent, Perl6StatementList.class);
        if (list == null) {
            return null;
        }
        List<Perl6StatementList> scopes = new ArrayList<>();
        while (list != null) {
            scopes.add(list);
            list = PsiTreeUtil.getParentOfType(list, Perl6StatementList.class);
        }
        return scopes;
    }

    protected PsiElement getAnchor(PsiElement parentToCreateAt, PsiElement[] elements) {
        PsiElement commonParent = PsiTreeUtil.findCommonParent(elements);
        if (parentToCreateAt == commonParent) return elements[0];
        // TODO
        return elements[0];
    }

    protected NewCodeBlockData getNewBlockData(Project project) {
        CompletableFuture<NewCodeBlockData> futureData = new CompletableFuture<>();
        TransactionGuard.getInstance().submitTransactionAndWait(() -> {
            Perl6ExtractMethodDialog dialog = new Perl6ExtractMethodDialog(project, TITLE, myCodeBlockType) {
                @Override
                protected void doAction() {
                    NewCodeBlockData data = new NewCodeBlockData(
                            myCodeBlockType, getScope(),
                            getName(), getReturnType(),
                            ArrayUtil.EMPTY_STRING_ARRAY
                    );
                    // TODO signature parts
                    futureData.complete(data);
                    closeOKAction();
                }

                @Override
                public void doCancelAction() {
                    futureData.complete(null);
                    close(1);
                }
            };
            dialog.show();
        });
        try {
            return futureData.get();
        }
        catch (InterruptedException|ExecutionException e) {
            return null;
        }
    }

    protected PsiElement createNewBlock(Project project, NewCodeBlockData data, List<String> contents) {
        return Perl6ElementFactory.createNamedCodeBlock(project, data, contents);
    }

    protected void insertNewCodeBlock(Project project, PsiElement parent,
                                      PsiElement newBlock, PsiElement anchor) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            parent.addBefore(newBlock, anchor);
        });
    }

    protected void replaceStatementsWithCall(Project project, NewCodeBlockData data, PsiElement parentScope, PsiElement[] elements) {
        PsiElement call;
        if (data.type == Perl6CodeBlockType.ROUTINE) {
            call = Perl6ElementFactory.createSubCall(parentScope.getProject(), data);
        } else {
            call = Perl6ElementFactory.createMethodCall(parentScope.getProject(), data);
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElement nextAnchor = elements[elements.length - 1].getNextSibling();
            // Delete present statements
            parentScope.deleteChildRange(elements[0], elements[elements.length - 1]);
            // Insert a call
            parentScope.addBefore(call, nextAnchor);
        });
    }

    private static void reportError(Project project, Editor editor, String message) {
        CommonRefactoringUtil.showErrorHint(project, editor, message, TITLE, null);
    }

    @Override
    public boolean isAvailableForQuickList(@NotNull Editor editor, @NotNull PsiFile file, @NotNull DataContext dataContext) {
        return false;
    }
}
