package org.raku.comma.psi;

public interface RakuVariableSource extends RakuPsiElement, RakuPsiDeclaration {
    String[] getVariableNames();
    RakuVariable[] getVariables();
}
