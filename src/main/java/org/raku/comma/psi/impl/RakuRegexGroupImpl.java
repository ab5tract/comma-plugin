package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuRegexAtom;
import org.raku.comma.psi.RakuRegexGroup;
import org.jetbrains.annotations.NotNull;

public class RakuRegexGroupImpl extends RakuASTWrapperPsiElement implements RakuRegexGroup {
    public RakuRegexGroupImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean mightMatchZeroWidth() {
        return atomsMightMatchZeroWidth(PsiTreeUtil.getChildrenOfType(this, RakuRegexAtom.class));
    }
}
