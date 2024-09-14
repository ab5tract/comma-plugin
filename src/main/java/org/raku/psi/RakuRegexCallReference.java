package org.raku.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.raku.psi.impl.RakuRegexCallImpl;
import org.raku.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RakuRegexCallReference extends PsiReferenceBase<RakuRegexCall> {
    private static final String[] PREDEFINED_METHODS = new String[]{"after", "digit", "before", "space", "ww", "upper", "wb", "cntrl", "ws", "graph", "xdigit", "ident", "lower", "punct", "print", "alnum", "alpha"};

    public RakuRegexCallReference(RakuRegexCallImpl call) {
        super(call, new TextRange(0, call.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        // First look for it lexically.
        RakuRegexCall call = getElement();
        RakuSymbol symbol = call.resolveLexicalSymbol(RakuSymbolKind.Regex, call.getText());
        if (symbol != null)
            return symbol.getPsi();

        // Otherwise, through the MOP.
        RakuPackageDecl selfType = call.getSelfType();
        if (selfType != null) {
            RakuSingleResolutionSymbolCollector collector = new RakuSingleResolutionSymbolCollector("." + call.getText(), RakuSymbolKind.Method);
            selfType.contributeMOPSymbols(collector, new MOPSymbolsAllowed(false, false, true, false));
            symbol = collector.getResult();
            if (symbol != null)
                return symbol.getPsi();
        }

        return null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        Collection<RakuSymbol> variants = getElement().getLexicalSymbolVariants(RakuSymbolKind.Regex);
        List<String> result = variants.isEmpty() ? new ArrayList<>() : ContainerUtil.map(variants, sym -> sym.getName());
        RakuPackageDecl selfType = getElement().getSelfType();
        if (selfType != null) {
            RakuVariantsSymbolCollector collector = new RakuVariantsSymbolCollector(RakuSymbolKind.Method);
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
        RakuRegexCall call = getElement();
        return call.setName(newElementName);
    }
}
