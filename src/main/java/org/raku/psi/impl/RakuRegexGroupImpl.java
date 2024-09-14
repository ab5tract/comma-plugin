package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuRegexAtom;
import org.raku.psi.RakuRegexGroup;
import org.jetbrains.annotations.NotNull;

public class RakuRegexGroupImpl extends ASTWrapperPsiElement implements RakuRegexGroup {
    public RakuRegexGroupImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean mightMatchZeroWidth() {
        return atomsMightMatchZeroWidth(PsiTreeUtil.getChildrenOfType(this, RakuRegexAtom.class));
    }
}
