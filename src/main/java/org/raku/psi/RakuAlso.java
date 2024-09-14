package org.raku.psi;

import org.raku.psi.symbols.RakuMOPSymbolContributor;

public interface RakuAlso extends RakuPsiElement, RakuMOPSymbolContributor {
    RakuTrait getTrait();
}
