package org.raku.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.parsing.RakuElementTypes;
import org.raku.psi.RakuNeedStatement;
import org.raku.psi.stub.RakuNeedStatementStub;

import java.util.List;

public class RakuNeedStatementStubImpl extends StubBase<RakuNeedStatement> implements RakuNeedStatementStub {
    private final List<String> moduleNames;

    public RakuNeedStatementStubImpl(StubElement parent, List<String> moduleNames) {
        super(parent, RakuElementTypes.NEED_STATEMENT);
        this.moduleNames = moduleNames;
    }

    @Override
    public List<String> getModuleNames() {
        return moduleNames;
    }
}
