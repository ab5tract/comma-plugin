package org.raku.comma.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.comma.psi.RakuUseStatement;

public interface RakuUseStatementStub extends StubElement<RakuUseStatement> {
    String getModuleName();
}
