package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6TypeNameReference extends PsiReferenceBase<Perl6PsiElement> {
    public Perl6TypeNameReference(@NotNull Perl6PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6PsiElement ref = getElement();
        String typeName = ref instanceof Perl6TypeName
                ? ((Perl6TypeName)ref).getTypeName()
                : ref.getText();
        Perl6Symbol result = ref.resolveLexicalSymbol(Perl6SymbolKind.TypeOrConstant, typeName);
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

    @NotNull
    @Override
    public Object[] getVariants() {
        Perl6PsiElement ref = getElement();
        return ref.getLexicalSymbolVariants(Perl6SymbolKind.TypeOrConstant)
            .stream()
            .filter(this::declarationInScope)
            .map(symbol -> symbol.getName())
            .toArray();
    }

    private boolean declarationInScope(Perl6Symbol symbol) {
        // Symbols imported only by name are fine, as are those from another
        // file.
        PsiElement psi = symbol.getPsi();
        if (psi == null)
            return true;
        Perl6PsiElement ref = getElement();
        if (psi.getContainingFile() != ref.getContainingFile())
            return true;

        // Otherwise, should be already declared.
        return psi.getTextOffset() < ref.getTextOffset();
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        Perl6TypeName type = (Perl6TypeName)getElement();
        return type.setName(newElementName);
    }
}
