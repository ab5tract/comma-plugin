package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.impl.Perl6SubCallNameImpl;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class Perl6SubCallReference extends PsiReferenceBase.Poly<Perl6SubCallName> {
    public Perl6SubCallReference(Perl6SubCallNameImpl call) {
        super(call, new TextRange(0, call.getTextLength()), false);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Perl6SubCallName call = getElement();
        List<Perl6Symbol> symbol = call.resolveLexicalSymbolAllowingMulti(Perl6SymbolKind.Routine, call.getCallName());
        if (symbol != null) {
            return symbol.stream()
                    .map(s -> s.getPsi())
                    .filter(p -> p != null)
                    .map(p -> new PsiElementResolveResult(p))
                    .collect(Collectors.toList())
                    .toArray(ResolveResult.EMPTY_ARRAY);
        }
        return ResolveResult.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return getElement().getLexicalSymbolVariants(Perl6SymbolKind.Routine, Perl6SymbolKind.TypeOrConstant)
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
