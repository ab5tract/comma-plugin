package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.impl.Perl6PackageDeclImpl;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Perl6VariableReference extends PsiReferenceBase<Perl6Variable> {
    public Perl6VariableReference(Perl6Variable var) {
        super(var, new TextRange(0, var.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6Variable var = getElement();
        String name = var.getVariableName();
        if (name == null)
            return null;
        if (Perl6Variable.getTwigil(name) == '!') {
            // Attribute; resolve through MOP.
            Perl6PackageDecl enclosingPackage = var.getSelfType();
            if (enclosingPackage != null) {
                Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector(name, Perl6SymbolKind.Variable);
                enclosingPackage.contributeMOPSymbols(collector, new MOPSymbolsAllowed(
                        true, true, true, enclosingPackage.getPackageKind().equals("role")));
                Perl6Symbol symbol = collector.getResult();
                if (symbol != null)
                    return symbol.getPsi();
            }
        }
        else {
            // Lexical; resolve through lexpad.
            Perl6Symbol symbol = var.resolveLexicalSymbol(Perl6SymbolKind.Variable, name);
            if (symbol != null) {
                PsiElement psi = symbol.getPsi();
                if (psi != null) {
                    if (psi.getContainingFile() != var.getContainingFile())
                        return psi;
                    if (psi.getTextOffset() <= var.getTextOffset() || Perl6Variable.getSigil(name) == '&')
                        return psi;
                }
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<Perl6Symbol> syms = new ArrayList<>(getElement().getLexicalSymbolVariants(Perl6SymbolKind.Variable));
        Perl6PackageDecl enclosingPackage = getElement().getSelfType();
        if (enclosingPackage != null) {
            Perl6VariantsSymbolCollector collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Variable);
            enclosingPackage.contributeMOPSymbols(collector, new MOPSymbolsAllowed(
                    true, true, true, enclosingPackage.getPackageKind().equals("role")));
            syms.addAll(collector.getVariants());
        }
        return syms
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
            return twigil == '!' || twigil == '.';
        }

        return false;
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return myElement.setName(newElementName);
    }
}
