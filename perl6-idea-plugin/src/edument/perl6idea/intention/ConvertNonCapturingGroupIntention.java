package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6RegexGroup;
import org.jetbrains.annotations.NotNull;

public abstract class ConvertNonCapturingGroupIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        Perl6RegexGroup group = PsiTreeUtil.getNonStrictParentOfType(element, Perl6RegexGroup.class);
        assert group != null;
        PsiElement capture = obtainReplacer(project, group);
        postProcess(project, editor, group.replace(capture));
    }

    @NotNull
    abstract PsiElement obtainReplacer(Project project, Perl6RegexGroup group);

    protected void postProcess(Project project, Editor editor, PsiElement element) {}

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return PsiTreeUtil.getNonStrictParentOfType(element, Perl6RegexGroup.class) != null;
    }
}
