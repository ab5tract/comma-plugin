package org.raku.psi;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import org.raku.psi.stub.RakuTypeNameStub;

public interface RakuTypeName extends RakuPsiElement, StubBasedPsiElement<RakuTypeNameStub>,
                                      PsiNamedElement, RakuExtractable {
    String getTypeName();
}
