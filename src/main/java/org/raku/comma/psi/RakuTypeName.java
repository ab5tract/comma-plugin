package org.raku.comma.psi;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import org.raku.comma.psi.stub.RakuTypeNameStub;

public interface RakuTypeName extends RakuPsiElement, StubBasedPsiElement<RakuTypeNameStub>,
                                      PsiNamedElement, RakuExtractable {
    String getTypeName();
}
