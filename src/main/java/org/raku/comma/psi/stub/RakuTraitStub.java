package org.raku.comma.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.comma.psi.RakuTrait;

public interface RakuTraitStub extends StubElement<RakuTrait> {
    String getTraitModifier();
    String getTraitName();
}
