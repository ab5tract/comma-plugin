package org.raku.psi;

public interface RakuRegexInfixApplication extends RakuRegexPsiElement {
    String getOperator();
    RakuRegexAtom[][] getOperandAtomSequences();
}
