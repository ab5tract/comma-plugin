package org.raku.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.RakuRegexGroup;
import org.jetbrains.annotations.NotNull;

public abstract class ConvertNonCapturingGroupIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        RakuRegexGroup group = PsiTreeUtil.getNonStrictParentOfType(element, RakuRegexGroup.class);
        assert group != null;
        PsiElement capture = obtainReplacer(project, group);
        postProcess(project, editor, group.replace(capture));
    }

    @NotNull
    abstract PsiElement obtainReplacer(Project project, RakuRegexGroup group);

    protected void postProcess(Project project, Editor editor, PsiElement element) {}

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return PsiTreeUtil.getNonStrictParentOfType(element, RakuRegexGroup.class) != null;
    }
}
