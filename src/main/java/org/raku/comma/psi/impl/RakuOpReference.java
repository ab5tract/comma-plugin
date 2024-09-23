package org.raku.comma.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.raku.comma.psi.*;
import org.raku.comma.psi.symbols.RakuSymbol;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RakuOpReference extends PsiReferenceBase.Poly<RakuPsiElement> {
    public RakuOpReference(RakuPsiElement operator) {
        super(operator, new TextRange(0, operator.getFirstChild().getTextLength()), false);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        RakuPsiElement op = getElement();

        String formatter = switch (op) {
            case RakuInfix rakuInfix -> "&infix:<%s>";
            case RakuPrefix rakuPrefix -> "&prefix:<%s>";
            case RakuPostfix rakuPostfix -> "&postfix:<%s>";
            case RakuSubCallName rakuTerm -> "&term:<%s>";
            default -> "";
        };
        if (! formatter.isEmpty()) {
            String name = formatter.formatted(op.getText());
            List<RakuSymbol> symbols = op.resolveLexicalSymbolAllowingMulti(RakuSymbolKind.Variable, name);

            if (! symbols.isEmpty()) {
                return symbols.stream()
                    .map(RakuSymbol::getPsi)
                    .filter(Objects::nonNull)
                    .map(PsiElementResolveResult::new)
                    .toArray(ResolveResult[]::new);
            }
        }
        return ResolveResult.EMPTY_ARRAY;
    }
}
