package org.raku.comma.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.raku.comma.psi.RakuInfix;
import org.raku.comma.psi.RakuPostfix;
import org.raku.comma.psi.RakuPrefix;
import org.raku.comma.psi.RakuPsiElement;
import org.raku.comma.psi.symbols.RakuSymbol;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RakuOpReference extends PsiReferenceBase.Poly<RakuPsiElement> {
    public RakuOpReference(RakuPsiElement operator) {
        super(operator, new TextRange(0, operator.getFirstChild().getTextLength()), false);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        RakuPsiElement op = getElement();

        String prefix = null;

        if (op instanceof RakuInfix) {
            prefix = "&infix:";
        }
        else if (op instanceof RakuPrefix) {
            prefix = "&prefix:";
        }
        else if (op instanceof RakuPostfix) {
            prefix = "&postfix:";
        }
        if (prefix != null) {
            String name = String.format("%s<%s>", prefix, op.getText());
            List<RakuSymbol> symbols = op.resolveLexicalSymbolAllowingMulti(RakuSymbolKind.Variable, name);

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
