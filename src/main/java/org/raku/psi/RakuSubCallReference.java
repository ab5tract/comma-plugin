package org.raku.psi;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.impl.RakuSubCallNameImpl;
import org.raku.psi.symbols.RakuSymbol;
import org.raku.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RakuSubCallReference extends PsiReferenceBase.Poly<RakuSubCallName> {
    private final boolean maybeCoercion;

    public RakuSubCallReference(RakuSubCallNameImpl call, boolean maybeCoercion) {
        super(call, new TextRange(0, call.getTextLength()), false);
        this.maybeCoercion = maybeCoercion;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        RakuSubCallName call = getElement();
        String name = call.getCallName();

        List<RakuSymbol> symbols = call.resolveLexicalSymbolAllowingMulti(RakuSymbolKind.Routine, name);
        if (symbols != null) {
            return symbols.stream()
                .map(s -> s.getPsi())
                .filter(p -> p != null)
                .map(p -> new PsiElementResolveResult(p))
                .toArray(ResolveResult[]::new);
        }

        if (maybeCoercion) {
            RakuSymbol type = call.resolveLexicalSymbol(RakuSymbolKind.TypeOrConstant, name);
            if (type != null && type.getPsi() != null)
                return new ResolveResult[] { new PsiElementResolveResult(type.getPsi()) };
        }

        return ResolveResult.EMPTY_ARRAY;
    }

    @Override
    public Object @NotNull [] getVariants() {
        return getElement().getLexicalSymbolVariants(RakuSymbolKind.Routine, RakuSymbolKind.TypeOrConstant)
            .stream()
            .map(sym -> {
                PsiElement psi = sym.getPsi();
                if (psi instanceof RakuRoutineDecl)
                    return strikeoutDeprecated(LookupElementBuilder.create(psi, sym.getName()).withTypeText(((RakuRoutineDecl)psi).summarySignature()), psi);
                else
                    return sym.getName();
            }).toArray();
    }

    private static LookupElementBuilder strikeoutDeprecated(LookupElementBuilder item, PsiElement psi) {
        return psi instanceof RakuDeprecatable && ((RakuDeprecatable)psi).isDeprecated()
               ? item.strikeout()
               : item;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        PsiElement call = myElement.getParent();
        if (call instanceof RakuSubCall subCall) {
            return subCall.setName(newElementName);
        }
        throw new IncorrectOperationException();
    }
}
