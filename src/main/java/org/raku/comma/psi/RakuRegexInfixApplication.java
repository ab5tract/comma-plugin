package org.raku.comma.psi;

public interface RakuRegexInfixApplication extends RakuRegexPsiElement {
    String getOperator();
    RakuRegexAtom[][] getOperandAtomSequences();
}
