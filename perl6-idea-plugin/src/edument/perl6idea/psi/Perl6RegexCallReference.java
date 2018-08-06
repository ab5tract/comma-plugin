package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import edument.perl6idea.psi.impl.Perl6RegexCallImpl;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Perl6RegexCallReference extends PsiReferenceBase<Perl6PsiElement> {
    private static final String[] PREDEFINED_METHODS = new String[]{"after", "digit", "before", "space", "ww", "upper", "wb", "cntrl", "ws", "graph", "xdigit", "ident", "lower", "punct", "print", "alnum", "alpha"};

    public Perl6RegexCallReference(Perl6RegexCallImpl call) {
        super(call, new TextRange(0, call.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6RegexCall call = (Perl6RegexCall)getElement();
        Perl6Symbol symbol = call.resolveSymbol(Perl6SymbolKind.Regex, call.getText());
        return symbol != null ? symbol.getPsi() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<String> result = getElement()
            .getSymbolVariants(Perl6SymbolKind.Regex)
            .stream()
            .map(sym -> sym.getName()).collect(toList());
        result.addAll(Arrays.asList(PREDEFINED_METHODS));
        return result.toArray();
    }
}
