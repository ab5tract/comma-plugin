package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuRegexAtom;
import org.raku.comma.psi.RakuRegexQuantifier;
import org.jetbrains.annotations.NotNull;

public class RakuRegexAtomImpl extends RakuASTWrapperPsiElement implements RakuRegexAtom {
    public RakuRegexAtomImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean mightMatchZeroWidth() {
        // See if it is quantified with ? or *, which can trivially match zero
        // width.
        RakuRegexQuantifier quantifier = PsiTreeUtil.getChildOfType(this, RakuRegexQuantifier.class);
        if (quantifier != null) {
            String quantText = quantifier.getFirstChild().getText();
            if (quantText.equals("?") || quantText.equals("*"))
                return true;
        }

        // Otherwise, not sure.
        return false;
    }
}
