package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.parsing.RakuTokenTypes;
import org.raku.comma.psi.RakuLabel;
import org.raku.comma.psi.symbols.RakuExplicitSymbol;
import org.raku.comma.psi.symbols.RakuSymbolCollector;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuLabelImpl extends ASTWrapperPsiElement implements RakuLabel {
    public RakuLabelImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull String getScope() {
        return "my";
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(RakuTokenTypes.LABEL_NAME);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public void contributeLexicalSymbols(RakuSymbolCollector collector) {
        collector.offerSymbol(new RakuExplicitSymbol(RakuSymbolKind.TypeOrConstant, this));
    }
}
