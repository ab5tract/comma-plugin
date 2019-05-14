package edument.perl6idea.refactoring.inline;

import com.intellij.lang.refactoring.InlineHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public class Perl6InlineRoutineUtil {
    public static final String REFACTORING_NAME = "Inline Routine";

    public static InlineHandler.Settings inlineRoutineSettings(Perl6RoutineDecl routine, Editor editor, boolean invokedOnReference) {
        Project project = routine.getProject();

        if (hasBadReturns(routine)) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Refactoring is not supported when return statement interrupts the execution flow", REFACTORING_NAME, null);
        }
        if (hasStateVariables(routine)) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Refactoring is not supported when state variables are present", REFACTORING_NAME, null);
        }

        PsiElement[] routineContent = routine.getContent();
        if (routineContent == null || routineContent.length == 0) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Refactoring cannot be applied to empty routine", REFACTORING_NAME, null);
            return InlineHandler.Settings.CANNOT_INLINE_SETTINGS;
        }

        return inlineCallDialogResult(project, editor, routine, invokedOnReference);
    }

    private static InlineHandler.Settings inlineCallDialogResult(Project project, Editor editor, Perl6RoutineDecl routine, boolean invokedOnReference) {
        PsiFile containingFile = routine.getContainingFile();
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAtCaret = containingFile.findElementAt(offset);
        PsiElement statement = PsiTreeUtil.getNonStrictParentOfType(elementAtCaret, Perl6Statement.class);

        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return () -> !(statement.getFirstChild() instanceof Perl6RoutineDecl);
        }

        InlinePerl6RoutineDialog dialog = new InlinePerl6RoutineDialog(project, routine, invokedOnReference);
        if (dialog.showAndGet()) {
            return dialog::isInlineThisOnly;
        } else {
            return InlineHandler.Settings.CANNOT_INLINE_SETTINGS;
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
                            if (Objects.equals(((Perl6ScopedDecl) parent).getScope(), "state")) {
                                return super.execute(each);
                            }
                        }
                    } else if (each instanceof Perl6RoutineDecl || each instanceof Perl6PackageDecl) {
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
        if (statements == null || statements.length == 0)
            return false;

        checkTrailingReturn(routine, returnStatements);
        return !returnStatements.isEmpty();
    }

    private static void checkTrailingReturn(@NotNull Perl6RoutineDecl routine, Collection<PsiElement> returnStatements) {
        PsiElement[] statements = routine.getContent();
        PsiElement last = statements[statements.length - 1];
        returnStatements.remove(last);
        // TODO
        //if (last instanceof Perl6IfStatement) {
        //    return checkTailIfStatement((Perl6IfStatement)last, returnStatements);
        //}
    }

    private static Collection<PsiElement> collectReturns(Perl6RoutineDecl routine) {
        PsiElementProcessor.CollectElements<PsiElement> processor =
            new PsiElementProcessor.CollectElements<PsiElement>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (each instanceof Perl6SubCall) {
                        if (Objects.equals(((Perl6SubCall) each).getSubCallName(), "return")) {
                            Perl6Statement statement = PsiTreeUtil.getParentOfType(each, Perl6Statement.class);
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
}
