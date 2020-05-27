package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.Perl6ImplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

public class Perl6BlockImpl extends ASTWrapperPsiElement implements Perl6Block {
    public Perl6BlockImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Perl6Blockoid getBlockoid() {
        return PsiTreeUtil.getChildOfType(this, Perl6Blockoid.class);
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        PsiElement parent = getParent();
        if (parent instanceof P6Topicalizer && ((P6Topicalizer)parent).isTopicalizing())
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$_", parent));
    }
}
