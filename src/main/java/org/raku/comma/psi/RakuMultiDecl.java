package org.raku.comma.psi;

public interface RakuMultiDecl extends RakuPsiElement {
    String getMultiness();
    RakuSignature getSignature();
}
