package edument.perl6idea.refactoring.inline.call;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.*;
import edument.perl6idea.refactoring.inline.IllegalInlineeException;
import edument.perl6idea.refactoring.inline.Perl6InlineActionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public class Perl6InlineCallActionHandler extends Perl6InlineActionHandler {
    @Override
    public boolean canInlineElement(PsiElement element) {
        return element instanceof Perl6RoutineDecl &&
               element.getNavigationElement() instanceof Perl6RoutineDecl &&
               element.getLanguage() instanceof Perl6Language;
    }

    @Override
    public void inlineElement(Project project, Editor editor, PsiElement element) {
        Perl6RoutineDecl routine = (Perl6RoutineDecl)element.getNavigationElement();

        PsiReference reference = editor != null ?
                                 TargetElementUtil.findReference(editor, editor.getCaretModel().getOffset()) : null;

        if (routine.getParent() instanceof Perl6MultiDecl) {
            String typeOfMulti = routine.getParent().getFirstChild().getText();
            if (Objects.equals(typeOfMulti, "proto"))
                reportError(project, editor, "inlining of routine with proto is not supported");
            else if (Objects.equals(typeOfMulti, "multi"))
                reportError(project, editor, "inlining of routine with multi is not supported");
            return;
        }

        if (reference != null) {
            final PsiElement refElement = reference.getElement();
            if (!isEnabledForLanguage(refElement.getLanguage())) {
                reportError(project, editor, "inlining of routine is not supported for " + refElement.getLanguage().getDisplayName());
                return;
            }
        }

        if (hasStateVariables(routine)) {
            reportError(project, editor, "state variables are present");
            return;
        }

        try {
            checkUnresolvedElements(routine, reference);
        }
        catch (IllegalInlineeException ex) {
            reportError(project, editor, ex.toString());
            return;
        }

        if (hasBadReturns(routine)) {
            reportError(project, editor, "return statement interrupts the execution flow");
                return;
        }

        PsiElement refElement = null;
        if (reference != null) {
            refElement = reference.getElement();
        }

        Perl6InlineCallDialog dialog = new Perl6InlineCallDialog(project, routine, refElement, editor);
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            dialog.doAction();
        } else {
            dialog.show();
        }
    }

    private static boolean hasStateVariables(Perl6RoutineDecl routine) {
        PsiElementProcessor.CollectElements<PsiElement> processor =
            new PsiElementProcessor.CollectElements<PsiElement>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (each instanceof Perl6VariableDecl) {
                        PsiElement parent = each.getParent();
                        if (parent instanceof Perl6ScopedDecl) {
                            if (Objects.equals(((Perl6ScopedDecl)parent).getScope(), "state")) {
                                return super.execute(each);
                            }
                        }
                    }
                    else if (each instanceof Perl6RoutineDecl || each instanceof Perl6PackageDecl) {
                        return false;
                    }
                    return true;
                }
            };
        for (PsiElement part : routine.getContent()) {
            PsiTreeUtil.processElements(part, processor);
        }
        return !processor.getCollection().isEmpty();
    }

    private static boolean hasBadReturns(Perl6RoutineDecl routine) {
        Collection<PsiElement> returnStatements = collectReturns(routine);
        PsiElement[] statements = routine.getContent();
        if (statements.length == 0) {
            return false;
        }

        checkTrailingReturn(routine, returnStatements);
        return !returnStatements.isEmpty();
    }

    private static void checkTrailingReturn(@NotNull Perl6RoutineDecl routine, Collection<PsiElement> returnStatements) {
        PsiElement[] statements = routine.getContent();
        PsiElement last = statements[statements.length - 1];
        returnStatements.remove(last);
    }

    private static Collection<PsiElement> collectReturns(Perl6RoutineDecl routine) {
        PsiElementProcessor.CollectElements<PsiElement> processor =
            new PsiElementProcessor.CollectElements<PsiElement>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (each instanceof Perl6SubCall) {
                        if (Objects.equals(((Perl6SubCall)each).getCallName(), "return")) {
                            Perl6Statement statement = PsiTreeUtil.getParentOfType(each, Perl6Statement.class);
                            assert statement != null;
                            return super.execute(statement);
                        }
                    }
                    return !(each instanceof Perl6RoutineDecl || each instanceof Perl6PackageDecl);
                }
            };
        PsiElement[] contents = routine.getContent();
        for (PsiElement statement : contents) {
            PsiTreeUtil.processElements(statement, processor);
        }
        return processor.getCollection();
    }

    protected void reportError(Project project, Editor editor, String reason) {
        CommonRefactoringUtil.showErrorHint(project, editor,
                String.format("Cannot perform inline refactoring: %s", reason),
                "Cannot inline routine", null);
    }
}
