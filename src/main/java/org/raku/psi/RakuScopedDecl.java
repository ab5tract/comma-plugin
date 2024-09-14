package org.raku.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.psi.stub.RakuScopedDeclStub;

public interface RakuScopedDecl extends RakuPsiElement, StubBasedPsiElement<RakuScopedDeclStub> {
    String getScope();
}
