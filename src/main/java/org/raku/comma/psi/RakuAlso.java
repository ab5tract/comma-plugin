package org.raku.comma.psi;

import org.raku.comma.psi.symbols.RakuMOPSymbolContributor;

public interface RakuAlso extends RakuPsiElement, RakuMOPSymbolContributor {
    RakuTrait getTrait();
}
