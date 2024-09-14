package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.highlighter.RakuHighlightVisitor;
import org.raku.comma.psi.RakuBlock;
import org.raku.comma.psi.RakuBlockoid;
import org.raku.comma.psi.RakuTopicalizer;
import org.raku.comma.psi.symbols.RakuImplicitSymbol;
import org.raku.comma.psi.symbols.RakuSymbolCollector;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;

public class RakuBlockImpl extends ASTWrapperPsiElement implements RakuBlock {
    public RakuBlockImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public RakuBlockoid getBlockoid() {
        return PsiTreeUtil.getChildOfType(this, RakuBlockoid.class);
    }

    @Override
    public void contributeScopeSymbols(RakuSymbolCollector collector) {
        PsiElement parent = getParent();
        if (parent instanceof RakuTopicalizer && ((RakuTopicalizer)parent).isTopicalizing())
            collector.offerSymbol(new RakuImplicitSymbol(RakuSymbolKind.Variable, "$_", parent));
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RakuHighlightVisitor) {
            ((RakuHighlightVisitor)visitor).visitRakuElement(this);
        } else {
            super.accept(visitor);
        }
    }
}
