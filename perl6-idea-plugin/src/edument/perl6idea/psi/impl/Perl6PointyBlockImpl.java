package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import edument.perl6idea.highlighter.RakuHighlightVisitor;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6PointyBlock;
import edument.perl6idea.psi.Perl6Signature;
import edument.perl6idea.psi.symbols.Perl6ImplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6PointyBlockImpl extends ASTWrapperPsiElement implements Perl6PointyBlock {
    public Perl6PointyBlockImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Perl6Parameter[] getParams() {
        Perl6Signature sig = findChildByClass(Perl6Signature.class);
        if (sig != null) return sig.getParameters();
        return new Perl6Parameter[]{};
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "&?BLOCK", this));
    }

    @Nullable
    @Override
    public PsiElement getTopic() {
        return findChildByClass(Perl6Signature.class);
    }

    @Override
    public String getLambda() {
        ASTNode lambda = getNode().findChildByType(Perl6TokenTypes.LAMBDA);
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
