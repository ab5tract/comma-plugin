package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuTrusts;
import org.raku.psi.RakuTypeName;

public class RakuTrustsImpl extends ASTWrapperPsiElement implements RakuTrusts {
    public RakuTrustsImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String getTypeName() {
        RakuTypeName name = PsiTreeUtil.findChildOfType(this, RakuTypeName.class);
        return name == null ? "" : name.getTypeName();
    }
}
