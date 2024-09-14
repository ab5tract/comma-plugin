package org.raku.comma.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.comma.parsing.RakuElementTypes;
import org.raku.comma.psi.RakuScopedDecl;
import org.raku.comma.psi.stub.RakuScopedDeclStub;

public class RakuScopedDeclStubImpl extends StubBase<RakuScopedDecl> implements RakuScopedDeclStub {
    private final String scope;

    public RakuScopedDeclStubImpl(StubElement parent, String scope) {
        super(parent, RakuElementTypes.SCOPED_DECLARATION);
        this.scope = scope;
    }

    @Override
    public String getScope() {
        return scope;
    }
}
