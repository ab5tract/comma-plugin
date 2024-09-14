package org.raku.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.psi.RakuTypeName;

public interface RakuTypeNameStub extends StubElement<RakuTypeName> {
    String getTypeName();
}
