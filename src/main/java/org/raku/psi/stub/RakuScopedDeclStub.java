package org.raku.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.psi.RakuScopedDecl;

public interface RakuScopedDeclStub extends StubElement<RakuScopedDecl> {
    String getScope();
}
