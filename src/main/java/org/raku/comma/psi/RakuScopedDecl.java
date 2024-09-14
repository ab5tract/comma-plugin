package org.raku.comma.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.comma.psi.stub.RakuScopedDeclStub;

public interface RakuScopedDecl extends RakuPsiElement, StubBasedPsiElement<RakuScopedDeclStub> {
    String getScope();
}
