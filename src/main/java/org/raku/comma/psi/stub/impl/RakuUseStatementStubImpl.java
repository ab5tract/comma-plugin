package org.raku.comma.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.comma.parsing.RakuElementTypes;
import org.raku.comma.psi.RakuUseStatement;
import org.raku.comma.psi.stub.RakuUseStatementStub;

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
