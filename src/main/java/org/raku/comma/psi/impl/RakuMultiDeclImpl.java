package org.raku.comma.psi.impl;

import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.raku.comma.parsing.RakuTokenTypes;
import org.raku.comma.psi.RakuMultiDecl;
import org.jetbrains.annotations.NotNull;
import org.raku.comma.psi.RakuSignature;

public class RakuMultiDeclImpl extends RakuASTWrapperPsiElement implements RakuMultiDecl {
    public RakuMultiDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getMultiness() {
        PsiElement multiness = findChildByType(RakuTokenTypes.MULTI_DECLARATOR);
        return multiness == null
               ? "only"
               : multiness.getText();
    }

    public RakuSignature getSignature() {
        return PsiTreeUtil.findChildOfType(this, RakuSignature.class);
    }
}
