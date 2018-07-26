package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6VariableReference extends PsiReferenceBase<Perl6PsiElement> {
    public Perl6VariableReference(Perl6Variable var) {
        super(var, new TextRange(0, var.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6Variable var = (Perl6Variable)getElement();
        Perl6Symbol symbol = var.resolveSymbol(Perl6SymbolKind.Variable, var.getVariableName());
        if (symbol != null) {
            PsiElement psi = symbol.getPsi();
            if (psi != null) {
                if (psi.getContainingFile() != var.getContainingFile())
                    return psi;
                if (psi.getTextOffset() > var.getTextOffset()) {
                    if (Perl6Variable.getSigil(var.getVariableName()) == '&' || Perl6Variable.getTwigil(var.getVariableName()) == '!')
                        return psi;
                }
                else {
                    return psi;
                }
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return getElement().getSymbolVariants(Perl6SymbolKind.Variable)
               .stream()
               .filter(this::isDeclaredAfterCurrentPosition)
               .map(sym -> sym.getName())
               .toArray();
    }

    private boolean isDeclaredAfterCurrentPosition(Perl6Symbol symbol) {
        PsiElement psi = symbol.getPsi();

        // No PSI element or imported from another file is always fine.
        if (psi == null)
            return true;
        if (psi.getContainingFile() != getElement().getContainingFile())
            return true;

        // Declared in this file before the current location is fine.
        if (psi.getTextOffset() < getElement().getTextOffset())
            return true;

        // Declared later, but routine or attribute is fine.
        String name = symbol.getName();
        if (name.length() >= 2) {
            if (name.charAt(0) == '&')
                return true;
            char twigil = name.charAt(1);
            if (twigil == '!' || twigil == '.')
                return true;
        }

        return false;
    }
}
