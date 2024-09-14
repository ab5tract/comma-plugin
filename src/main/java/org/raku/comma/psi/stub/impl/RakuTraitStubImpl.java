package org.raku.comma.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.comma.parsing.RakuElementTypes;
import org.raku.comma.psi.RakuTrait;
import org.raku.comma.psi.stub.RakuTraitStub;

public class RakuTraitStubImpl extends StubBase<RakuTrait> implements RakuTraitStub {
    private final String modifier;
    private final String name;

    public RakuTraitStubImpl(StubElement parent, String modifier, String name) {
        super(parent, RakuElementTypes.TRAIT);
        this.modifier = modifier;
        this.name = name;
    }

    @Override
    public String getTraitModifier() {
        return modifier;
    }

    @Override
    public String getTraitName() {
        return name;
    }
}
