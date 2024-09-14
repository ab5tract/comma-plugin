package org.raku.comma.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.comma.psi.RakuScopedDecl;

public interface RakuScopedDeclStub extends StubElement<RakuScopedDecl> {
    String getScope();
}
