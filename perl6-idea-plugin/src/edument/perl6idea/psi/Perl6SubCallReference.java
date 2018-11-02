package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.impl.Perl6SubCallNameImpl;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6SubCallReference extends PsiReferenceBase<Perl6SubCallName> {
    public Perl6SubCallReference(Perl6SubCallNameImpl call) {
        super(call, new TextRange(0, call.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6SubCallName call = getElement();
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

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        PsiElement call = myElement.getParent();
        if (call instanceof Perl6SubCall) {
            Perl6SubCall subCall = (Perl6SubCall)call;
            return subCall.setName(newElementName);
        }
        throw new IncorrectOperationException();
    }
}
