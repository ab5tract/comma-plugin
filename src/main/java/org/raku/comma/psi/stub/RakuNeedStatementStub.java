package org.raku.comma.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.comma.psi.RakuNeedStatement;

import java.util.List;

public interface RakuNeedStatementStub extends StubElement<RakuNeedStatement> {
    List<String> getModuleNames();
}
