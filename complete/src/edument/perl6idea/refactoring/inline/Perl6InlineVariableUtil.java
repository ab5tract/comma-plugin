package edument.perl6idea.refactoring.inline;

import com.intellij.lang.refactoring.InlineHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import edument.perl6idea.psi.*;

public class Perl6InlineVariableUtil {
    static InlineHandler.Settings inlineVariableSettings(PsiElement element, Editor editor, boolean reference) {
        Project project = element.getProject();
        boolean hasInitializer;
        if (element instanceof Perl6VariableDecl) {
            hasInitializer = ((Perl6VariableDecl) element).hasInitializer();
        } else if (element instanceof Perl6ParameterVariable) {
            Perl6Parameter parameter = PsiTreeUtil.getParentOfType(element, Perl6Parameter.class);
            hasInitializer = parameter != null && parameter.getInitializer() != null;
        } else {
            String message = String.format("%s refactoring unsupported here", Perl6VariableInlineHandler.INLINE_VARIABLE);
            CommonRefactoringUtil.showErrorHint(project, editor, message, Perl6VariableInlineHandler.INLINE_VARIABLE, null);
            return InlineHandler.Settings.CANNOT_INLINE_SETTINGS;
        }

        if (!hasInitializer) {
            String message = String.format("%s refactoring is supported only when the initializer is present", Perl6VariableInlineHandler.INLINE_VARIABLE);
            CommonRefactoringUtil.showErrorHint(project, editor, message, Perl6VariableInlineHandler.INLINE_VARIABLE, null);
            return InlineHandler.Settings.CANNOT_INLINE_SETTINGS;
        }

        return inlineVariableDialogResult(project, editor, (PsiNamedElement) element, reference);
    }

    private static InlineHandler.Settings inlineVariableDialogResult(Project project, Editor editor, PsiNamedElement element, boolean reference) {
        PsiFile containingFile = element.getContainingFile();
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAtCaret = containingFile.findElementAt(offset);
        PsiElement statement = PsiTreeUtil.getParentOfType(elementAtCaret, Perl6Statement.class);
        Perl6Variable variable = PsiTreeUtil.getNonStrictParentOfType(elementAtCaret, Perl6Variable.class);
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return new Perl6InlineVariableSettings(variable) {
                @Override
                public boolean isOnlyOneReferenceToInline() {
                    return !(statement.getFirstChild() instanceof Perl6ScopedDecl || statement.getFirstChild() instanceof Perl6RoutineDecl);
                }
            };
        }

        InlinePerl6VariableDialog dialog = new InlinePerl6VariableDialog(project, element, reference);
        if (dialog.showAndGet()) {
            return new Perl6InlineVariableSettings(variable) {
                @Override
                public boolean isOnlyOneReferenceToInline() {
                    return dialog.isInlineThisOnly();
                }
            };
        } else {
            return InlineHandler.Settings.CANNOT_INLINE_SETTINGS;
        }
    }
}
