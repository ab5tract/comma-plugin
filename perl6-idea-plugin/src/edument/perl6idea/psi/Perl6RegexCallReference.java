package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.impl.Perl6RegexCallImpl;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
        // First look for it lexically.
        Perl6RegexCall call = getElement();
        Perl6Symbol symbol = call.resolveLexicalSymbol(Perl6SymbolKind.Regex, call.getText());
        if (symbol != null)
            return symbol.getPsi();

        // Otherwise, through the MOP.
        Perl6PackageDecl selfType = call.getSelfType();
        if (selfType != null) {
            Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector("." + call.getText(), Perl6SymbolKind.Method);
            selfType.contributeMOPSymbols(collector, new MOPSymbolsAllowed(false, false, true, false));
            symbol = collector.getResult();
            if (symbol != null)
                return symbol.getPsi();
        }

        return null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        Collection<Perl6Symbol> variants = getElement().getLexicalSymbolVariants(Perl6SymbolKind.Regex);
        List<String> result = variants.isEmpty() ? new ArrayList<>() : ContainerUtil.map(variants, sym -> sym.getName());
        Perl6PackageDecl selfType = getElement().getSelfType();
        if (selfType != null) {
            Perl6VariantsSymbolCollector collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);
            selfType.contributeMOPSymbols(collector, new MOPSymbolsAllowed(false, false, true, false));
            result.addAll(collector.getVariants()
                    .stream()
                    // Filter out external symbols for regex-calls
                    .filter(symbol -> !symbol.isExternal() )
                    // Delete first `.`, as we already have one in method (e.g. `.alpha`)
                    .map(sym -> sym.getName().substring(1)).toList());
        }
        result.addAll(Arrays.asList(PREDEFINED_METHODS));
        return result.toArray();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        Perl6RegexCall call = getElement();
        return call.setName(newElementName);
    }
}
