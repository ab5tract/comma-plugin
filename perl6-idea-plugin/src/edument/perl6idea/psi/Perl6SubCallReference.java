package edument.perl6idea.psi;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
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

import java.util.List;

public class Perl6SubCallReference extends PsiReferenceBase.Poly<Perl6SubCallName> {
    private final boolean maybeCoercion;

    public Perl6SubCallReference(Perl6SubCallNameImpl call, boolean maybeCoercion) {
        super(call, new TextRange(0, call.getTextLength()), false);
        this.maybeCoercion = maybeCoercion;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        Perl6SubCallName call = getElement();
        String name = call.getCallName();

        List<Perl6Symbol> symbols = call.resolveLexicalSymbolAllowingMulti(Perl6SymbolKind.Routine, name);
        if (symbols != null) {
            return symbols.stream()
                .map(s -> s.getPsi())
                .filter(p -> p != null)
                .map(p -> new PsiElementResolveResult(p))
                .toArray(ResolveResult[]::new);
        }

        if (maybeCoercion) {
            Perl6Symbol type = call.resolveLexicalSymbol(Perl6SymbolKind.TypeOrConstant, name);
            if (type != null && type.getPsi() != null)
                return new ResolveResult[] { new PsiElementResolveResult(type.getPsi()) };
        }

        return ResolveResult.EMPTY_ARRAY;
    }

    @Override
    public Object @NotNull [] getVariants() {
        return getElement().getLexicalSymbolVariants(Perl6SymbolKind.Routine, Perl6SymbolKind.TypeOrConstant)
            .stream()
            .map(sym -> {
                PsiElement psi = sym.getPsi();
                if (psi instanceof Perl6RoutineDecl)
                    return strikeoutDeprecated(LookupElementBuilder.create(psi, sym.getName()).withTypeText(((Perl6RoutineDecl)psi).summarySignature()), psi);
                else
                    return sym.getName();
            }).toArray();
    }

    private static LookupElementBuilder strikeoutDeprecated(LookupElementBuilder item, PsiElement psi) {
        return psi instanceof Perl6Deprecatable && ((Perl6Deprecatable)psi).isDeprecated()
               ? item.strikeout()
               : item;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        PsiElement call = myElement.getParent();
        if (call instanceof Perl6SubCall) {
            Perl6SubCall subCall = (Perl6SubCall)call;
            return subCall.setName(newElementName);
        }
        throw new IncorrectOperationException();
    }
}
