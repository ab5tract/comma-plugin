package org.raku.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.psi.RakuUseStatement;

public interface RakuUseStatementStub extends StubElement<RakuUseStatement> {
    String getModuleName();
}
