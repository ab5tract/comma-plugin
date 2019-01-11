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
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
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
    private static final String TITLE = "Code Block Extraction";
    private static final List<String> packageTypesWithMethods = new ArrayList<>(
            Arrays.asList("class", "role", "grammar", "monitor"));
    protected final Perl6CodeBlockType myCodeBlockType;

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

    protected void invoke(@NotNull Project project, Editor editor, PsiFile file, Perl6StatementList parentScope, PsiElement[] elements) {
        if (checkElementsSanity(project, editor, parentScope, elements)) return;
        if (checkMethodSanity(project, editor, parentScope)) return;

        /* Anchor represents an element, that will be a next to created one or null,
         * if no such anchor is possible, e.g. when a method is added after last one in a class */
        PsiElement anchor = getAnchor(parentScope, elements);

        NewCodeBlockData newCodeBlockData = getNewBlockData(project, elements);
        // If user cancelled action or exception occurred
        if (newCodeBlockData == null) return;
        List<String> contents = Arrays.stream(elements).map(PsiElement::getText).collect(Collectors.toList());
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
                String scopeKind = ((Perl6PackageDecl) wrapper).getPackageKind();
                if (!packageTypesWithMethods.contains(scopeKind)) {
                    reportError(project, editor, "Cannot extract a method into " + scopeKind);
                    return true;
                }
            } else {
                reportError(project, editor, "Cannot extract a method outside of a class, a monitor, a grammar or a role");
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
        PsiElement list = PsiTreeUtil.getNonStrictParentOfType(commonParent, Perl6StatementList.class);
        if (list == null) {
            return new ArrayList<>();
        }

        List<Perl6StatementList> scopes = new ArrayList<>();

        // Subroutine can be in any scope above, but methods are restricted
        if (myCodeBlockType != Perl6CodeBlockType.ROUTINE) {
            while (true) {
                Perl6PackageDecl methodScope = PsiTreeUtil.getParentOfType(list, Perl6PackageDecl.class);
                if (methodScope == null) break;
                String packageKind = methodScope.getPackageKind();
                if (packageTypesWithMethods.contains(packageKind)) {
                    Perl6StatementList newList = PsiTreeUtil.findChildOfType(methodScope, Perl6StatementList.class);
                    if (newList != null) {
                        scopes.add(newList);
                        list = methodScope;
                    } else {
                        break;
                    }
                } else {
                    list = methodScope;
                }
            }
        } else {
            while (list != null) {
                scopes.add((Perl6StatementList) list);
                list = PsiTreeUtil.getParentOfType(list, Perl6StatementList.class);
            }
        }
        return scopes;
    }

    protected PsiElement getAnchor(Perl6StatementList parentToCreateAt, PsiElement[] elements) {
        PsiElement commonParent = PsiTreeUtil.findCommonParent(elements);
        if (parentToCreateAt == commonParent) return elements[0];

        return PsiTreeUtil.findFirstParent(commonParent, e -> e.getParent() == parentToCreateAt);
    }

    protected NewCodeBlockData getNewBlockData(Project project, PsiElement[] elements) {
        CompletableFuture<NewCodeBlockData> futureData = new CompletableFuture<>();
        TransactionGuard.getInstance().submitTransactionAndWait(() -> {
            Perl6ExtractBlockDialog dialog = new Perl6ExtractBlockDialog(project, TITLE, myCodeBlockType, getCapturedVariables(elements)) {
                @Override
                protected void doAction() {
                    NewCodeBlockData data = new NewCodeBlockData(
                            myCodeBlockType, getScope(),
                            getName(), getReturnType(),
                            getInputVariables()
                    );
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

    protected Perl6VariableData[] getCapturedVariables(PsiElement[] elements) {
        List<Perl6VariableData> vars = new ArrayList<>();
        List<Perl6Variable> rawVars = new ArrayList<>();
        List<Perl6VariableDecl> rawVarDecls = new ArrayList<>();
        for (PsiElement el : elements) {
            rawVars.addAll(PsiTreeUtil.findChildrenOfType(el, Perl6Variable.class));
            rawVarDecls.addAll(PsiTreeUtil.findChildrenOfType(el, Perl6VariableDecl.class));
        }
        for (Perl6Variable var : rawVars) {
            String typeInferred = var.inferType();
            vars.add(new Perl6VariableData(var.getVariableName(), typeInferred));
        }
        return vars.toArray(new Perl6VariableData[0]);
    }

    protected PsiElement createNewBlock(Project project, NewCodeBlockData data, List<String> contents) {
        return Perl6ElementFactory.createNamedCodeBlock(project, data, contents);
    }

    protected void insertNewCodeBlock(Project project, PsiElement parent,
                                      PsiElement newBlock, PsiElement anchor) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (myCodeBlockType == Perl6CodeBlockType.ROUTINE) {
                parent.addBefore(newBlock, anchor);
            } else {
                parent.addAfter(newBlock, anchor);
            }

        });
    }

    protected void replaceStatementsWithCall(Project project, NewCodeBlockData data, PsiElement parentScope, PsiElement[] elements) {
        Perl6Statement call;
        if (data.type == Perl6CodeBlockType.ROUTINE) {
            call = Perl6ElementFactory.createSubCall(parentScope.getProject(), data);
        } else {
            call = Perl6ElementFactory.createMethodCall(parentScope.getProject(), data);
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElement closestParent = elements[0].getParent();
            // Insert a call
            closestParent.addBefore(call, elements[elements.length - 1].getNextSibling());
            // Delete present statements
            closestParent.deleteChildRange(elements[0], elements[elements.length - 1]);

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
