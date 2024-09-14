package org.raku.comma.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.comma.psi.RakuTypeName;

public interface RakuTypeNameStub extends StubElement<RakuTypeName> {
    String getTypeName();
}
