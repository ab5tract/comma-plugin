package org.raku.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.psi.RakuNeedStatement;

import java.util.List;

public interface RakuNeedStatementStub extends StubElement<RakuNeedStatement> {
    List<String> getModuleNames();
}
