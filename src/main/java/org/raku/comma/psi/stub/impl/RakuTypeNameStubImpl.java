package org.raku.comma.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.comma.parsing.RakuElementTypes;
import org.raku.comma.psi.RakuTypeName;
import org.raku.comma.psi.stub.RakuTypeNameStub;

public class RakuTypeNameStubImpl extends StubBase<RakuTypeName> implements RakuTypeNameStub {
    private final String typeName;

    public RakuTypeNameStubImpl(StubElement parent, String name) {
        super(parent, RakuElementTypes.TYPE_NAME);
        this.typeName = name;
    }


    @Override
    public String getTypeName() {
        return typeName;
    }
}
