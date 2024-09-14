package org.raku.comma.psi.symbols;

import org.raku.comma.psi.RakuPsiElement;

/**
 * Contributes lexical symbols; since packages are ultimately chained of lexical
 * environment too, this also includes package-scoped things like `class Foo::Bar [ }`.
 * It excludes things that are installed on meta-objects, such as attributes, methods,
 * and private methods.
 */
public interface RakuLexicalSymbolContributor extends RakuPsiElement {
    /**
     * Called with a collector to contribute lexically installed symbols.
     * @param collector
     */
    void contributeLexicalSymbols(RakuSymbolCollector collector);
}
