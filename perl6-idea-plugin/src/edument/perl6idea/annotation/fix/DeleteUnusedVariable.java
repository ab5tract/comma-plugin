package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;

public class DeleteUnusedVariable implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Safe delete unused variable";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Raku";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement caretEl = file.findElementAt(editor.getCaretModel().getOffset());
        if (caretEl == null)
            return;
        Perl6Variable variable = PsiTreeUtil.getNonStrictParentOfType(caretEl, Perl6Variable.class);
        Perl6VariableDecl decl = PsiTreeUtil.getParentOfType(caretEl, Perl6VariableDecl.class);
        assert decl != null;
        Perl6PsiUtil.deleteElementDocComments(PsiTreeUtil.getParentOfType(decl, Perl6Statement.class));
        decl.removeVariable(variable);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
