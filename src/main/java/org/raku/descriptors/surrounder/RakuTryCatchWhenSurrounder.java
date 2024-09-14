package org.raku.descriptors.surrounder;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuTry;
import org.raku.psi.RakuWhenStatement;

public class RakuTryCatchWhenSurrounder extends RakuGenericTrySurrounder<RakuTry> {
    public RakuTryCatchWhenSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected String createBranch() {
        return "when True {}";
    }

    @Override
    protected PsiElement getAnchor(RakuTry surrounder) {
        RakuWhenStatement whenStatement = PsiTreeUtil.findChildOfType(surrounder.getBlock(), RakuWhenStatement.class);
        return whenStatement == null ? null : whenStatement.getTopic();
    }

    @Override
    public String getTemplateDescription() {
        return "try { CATCH { when } }";
    }
}
