package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.impl.Perl6RegexCallImpl;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Perl6RegexCallReference extends PsiReferenceBase<Perl6RegexCall> {
    private static final String[] PREDEFINED_METHODS = new String[]{"after", "digit", "before", "space", "ww", "upper", "wb", "cntrl", "ws", "graph", "xdigit", "ident", "lower", "punct", "print", "alnum", "alpha"};

    public Perl6RegexCallReference(Perl6RegexCallImpl call) {
        super(call, new TextRange(0, call.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6RegexCall call = getElement();
        Perl6Symbol symbol = call.resolveLexicalSymbol(Perl6SymbolKind.Regex, call.getText());
        return symbol != null ? symbol.getPsi() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<String> result = getElement()
            .getLexicalSymbolVariants(Perl6SymbolKind.Regex)
            .stream()
            .map(sym -> sym.getName()).collect(toList());
        result.addAll(
            getElement().getLexicalSymbolVariants(Perl6SymbolKind.Method)
                        .stream()
                        // Filter out external symbols for regex-calls
                        .filter(symbol -> !symbol.isExternal() )
                        // Delete first `.`, as we already have one in method (e.g. `.alpha`)
                        .map(sym -> sym.getName().substring(1)).collect(toList())
        );
        result.addAll(Arrays.asList(PREDEFINED_METHODS));
        return result.toArray();
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        Perl6RegexCall call = getElement();
        return call.setName(newElementName);
    }
}
