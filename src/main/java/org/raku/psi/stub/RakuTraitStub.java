package org.raku.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.psi.RakuTrait;

public interface RakuTraitStub extends StubElement<RakuTrait> {
    String getTraitModifier();
    String getTraitName();
}
