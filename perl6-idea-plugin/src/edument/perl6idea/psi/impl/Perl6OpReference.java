package edument.perl6idea.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import edument.perl6idea.psi.Perl6Infix;
import edument.perl6idea.psi.Perl6Postfix;
import edument.perl6idea.psi.Perl6Prefix;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Perl6OpReference extends PsiReferenceBase.Poly<Perl6PsiElement> {
    public Perl6OpReference(Perl6PsiElement operator) {
        super(operator, new TextRange(0, operator.getTextLength()), false);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        Perl6PsiElement op = getElement();

        String prefix = null;

        if (op instanceof Perl6Infix) {
            prefix = "&infix:";
        }
        else if (op instanceof Perl6Prefix) {
            prefix = "&prefix:";
        }
        else if (op instanceof Perl6Postfix) {
            prefix = "&postfix:";
        }
        if (prefix != null) {
            String name = String.format("%s<%s>", prefix, op.getText());
            List<Perl6Symbol> symbols = op.resolveLexicalSymbolAllowingMulti(Perl6SymbolKind.Variable, name);

            if (symbols != null) {
                return symbols.stream()
                    .map(s -> s.getPsi())
                    .filter(p -> p != null)
                    .map(p -> new PsiElementResolveResult(p))
                    .toArray(ResolveResult[]::new);
            }
        }
        return ResolveResult.EMPTY_ARRAY;
    }
}
