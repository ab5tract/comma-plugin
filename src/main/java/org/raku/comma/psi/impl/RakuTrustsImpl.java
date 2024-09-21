package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuTrusts;
import org.raku.comma.psi.RakuTypeName;

public class RakuTrustsImpl extends RakuASTWrapperPsiElement implements RakuTrusts {
    public RakuTrustsImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String getTypeName() {
        RakuTypeName name = PsiTreeUtil.findChildOfType(this, RakuTypeName.class);
        return name == null ? "" : name.getTypeName();
    }
}
