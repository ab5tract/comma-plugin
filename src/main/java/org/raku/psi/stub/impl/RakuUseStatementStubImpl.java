package org.raku.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.parsing.RakuElementTypes;
import org.raku.psi.RakuUseStatement;
import org.raku.psi.stub.RakuUseStatementStub;

public class RakuUseStatementStubImpl extends StubBase<RakuUseStatement> implements RakuUseStatementStub {
    private final String moduleName;

    public RakuUseStatementStubImpl(StubElement parent, String moduleName) {
        super(parent, RakuElementTypes.USE_STATEMENT);
        this.moduleName = moduleName;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }
}
