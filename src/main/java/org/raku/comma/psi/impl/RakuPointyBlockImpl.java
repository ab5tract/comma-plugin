package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.raku.comma.highlighter.RakuHighlightVisitor;
import org.raku.comma.parsing.RakuTokenTypes;
import org.raku.comma.psi.RakuParameter;
import org.raku.comma.psi.RakuPointyBlock;
import org.raku.comma.psi.RakuSignature;
import org.raku.comma.psi.symbols.RakuImplicitSymbol;
import org.raku.comma.psi.symbols.RakuSymbolCollector;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuPointyBlockImpl extends ASTWrapperPsiElement implements RakuPointyBlock {
    public RakuPointyBlockImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public RakuParameter[] getParams() {
        RakuSignature sig = findChildByClass(RakuSignature.class);
        if (sig != null) return sig.getParameters();
        return new RakuParameter[]{};
    }

    @Override
    public void contributeScopeSymbols(RakuSymbolCollector collector) {
        collector.offerSymbol(new RakuImplicitSymbol(RakuSymbolKind.Variable, "&?BLOCK", this));
    }

    @Nullable
    @Override
    public PsiElement getTopic() {
        return findChildByClass(RakuSignature.class);
    }

    @Override
    public String getLambda() {
        ASTNode lambda = getNode().findChildByType(RakuTokenTypes.LAMBDA);
        return lambda == null ? "->" : lambda.getText();
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
