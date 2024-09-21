package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuNamedParameter;
import org.raku.comma.psi.RakuParameterVariable;
import org.jetbrains.annotations.NotNull;

import static org.raku.comma.parsing.RakuTokenTypes.NAMED_PARAMETER_NAME_ALIAS;

public class RakuNamedParameterImpl extends RakuASTWrapperPsiElement implements RakuNamedParameter {
    public RakuNamedParameterImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String summary() {
        PsiElement alias = findChildByType(NAMED_PARAMETER_NAME_ALIAS);
        RakuParameterVariable var = PsiTreeUtil.findChildOfType(this, RakuParameterVariableImpl.class);
        String base = ":";
        if (alias != null) {
            RakuNamedParameter internal = PsiTreeUtil.findChildOfType(this, RakuNamedParameterImpl.class);
            if (internal == null)
                return base + "$" + alias.getText();
            else
                return base + alias.getText() + "(" + internal.summary() + ")";
        }
        else if (var != null)
            return base + var.getText();
        else
            return base; // should not happen
    }
}
