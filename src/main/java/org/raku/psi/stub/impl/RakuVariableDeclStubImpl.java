package org.raku.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.parsing.RakuElementTypes;
import org.raku.psi.RakuVariableDecl;
import org.raku.psi.stub.RakuScopedDeclStub;
import org.raku.psi.stub.RakuVariableDeclStub;

public class RakuVariableDeclStubImpl extends StubBase<RakuVariableDecl> implements RakuVariableDeclStub {
    private final String[] variableNames;
    private final String variableType;
    private final boolean isExported;

    public RakuVariableDeclStubImpl(StubElement stub, String[] names, String type, boolean exported) {
        super(stub, RakuElementTypes.VARIABLE_DECLARATION);
        this.variableNames = names;
        this.variableType = type;
        isExported = exported;
    }

    @Override
    public String[] getVariableNames() {
        return variableNames;
    }

    @Override
    public String getVariableType() {
        return variableType;
    }

    @Override
    public String getScope() {
        return getParentStub() instanceof RakuScopedDeclStub
               ? ((RakuScopedDeclStub)getParentStub()).getScope()
               : ""; /* Shouldn't ever happen */
    }

    @Override
    public boolean isExported() {
        return isExported;
    }
}
