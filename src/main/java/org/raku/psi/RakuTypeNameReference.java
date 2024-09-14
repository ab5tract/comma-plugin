package org.raku.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.symbols.RakuSymbol;
import org.raku.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuTypeNameReference extends PsiReferenceBase<RakuPsiElement> {
    public RakuTypeNameReference(@NotNull RakuPsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        RakuPsiElement ref = getElement();
        String typeName = ref instanceof RakuTypeName
                ? ((RakuTypeName)ref).getTypeName()
                : ref.getText();
        RakuSymbol result = ref.resolveLexicalSymbol(RakuSymbolKind.TypeOrConstant, typeName);
        if (result != null) {
            PsiElement psi = result.getPsi();
            if (psi != null) {
                // It's fine if it's either imported or declared ahead of the point
                // it is being referenced.
                if (psi.getContainingFile() != ref.getContainingFile())
                    return psi;
                if (psi.getTextOffset() < ref.getTextOffset() || result.wasDeferred())
                    return psi;
            }
        }
        return null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        RakuPsiElement ref = getElement();
        return ref.getLexicalSymbolVariants(RakuSymbolKind.TypeOrConstant)
            .stream()
            .filter(this::declarationInScope)
            .map(symbol -> symbol.getName())
            .toArray();
    }

    private boolean declarationInScope(RakuSymbol symbol) {
        // Symbols imported only by name are fine, as are those from another
        // file.
        PsiElement psi = symbol.getPsi();
        if (psi == null)
            return true;
        RakuPsiElement ref = getElement();
        if (psi.getContainingFile() != ref.getContainingFile())
            return true;

        // Otherwise, should be already declared.
        return psi.getTextOffset() < ref.getTextOffset();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        RakuTypeName type = (RakuTypeName)getElement();
        return type.setName(newElementName);
    }
}
