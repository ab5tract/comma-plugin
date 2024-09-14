package org.raku.comma.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.comma.parsing.RakuElementTypes;
import org.raku.comma.psi.RakuSubset;
import org.raku.comma.psi.stub.RakuScopedDeclStub;
import org.raku.comma.psi.stub.RakuSubsetStub;

public class RakuSubsetStubImpl extends StubBase<RakuSubset> implements RakuSubsetStub {
    private final String subsetName;
    private final boolean isExported;
    private final String baseTypeName;

    public RakuSubsetStubImpl(StubElement stub, String name, boolean exported, String typeName) {
        super(stub, RakuElementTypes.SUBSET);
        subsetName = name;
        isExported = exported;
        baseTypeName = typeName;
    }

    @Override
    public String getTypeName() {
        return subsetName;
    }

    @Override
    public String getScope() {
        return getParentStub() instanceof RakuScopedDeclStub
               ? ((RakuScopedDeclStub)getParentStub()).getScope()
               : "our";
    }

    @Override
    public boolean isExported() {
        return isExported;
    }

    @Override
    public String getSubsetBaseTypeName() {
        return baseTypeName;
    }
}
