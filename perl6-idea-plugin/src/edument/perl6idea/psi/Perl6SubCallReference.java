package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import edument.perl6idea.psi.impl.Perl6SubCallNameImpl;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6SubCallReference extends PsiReferenceBase<Perl6PsiElement> {
    public Perl6SubCallReference(Perl6SubCallNameImpl call) {
        super(call, new TextRange(0, call.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6SubCallName call = (Perl6SubCallName)getElement();
        Perl6Symbol symbol = call.resolveSymbol(Perl6SymbolKind.Routine, call.getCallName());
        return symbol != null ? symbol.getPsi() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return getElement().getSymbolVariants(Perl6SymbolKind.Routine)
                           .stream()
                           .map(sym -> sym.getName())
                           .toArray();
    }
}
