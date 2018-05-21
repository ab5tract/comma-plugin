package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.Perl6ImplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

public class Perl6BlockImpl extends ASTWrapperPsiElement implements Perl6Block {
    private static final Class[] TOPICALIZERS = new Class[] {
        Perl6GivenStatement.class, Perl6ForStatement.class, Perl6WithoutStatement.class
    };

    public Perl6BlockImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void contributeExtraSymbols(Perl6SymbolCollector collector) {
        PsiElement parent = getParent();
        boolean isTopicalizer = false;

        // If the parent is an `if`, we need to look more carefully at it.
        if (parent instanceof Perl6IfStatement) {
            isTopicalizer = ((Perl6IfStatement)parent).getLeadingStatementControl().equals("with");
        }
        // Some other things are always unconditionally topicalizers.
        else {
            for (Class c : TOPICALIZERS) {
                if (c.isAssignableFrom(parent.getClass())) {
                    isTopicalizer = true;
                    break;
                }
            }
        }

        if (isTopicalizer)
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$_", parent));
    }
}
