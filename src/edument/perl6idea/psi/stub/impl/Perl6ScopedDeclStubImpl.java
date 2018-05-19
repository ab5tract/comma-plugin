package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6ScopedDecl;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;

public class Perl6ScopedDeclStubImpl extends StubBase<Perl6ScopedDecl> implements Perl6ScopedDeclStub {
    private String scope;
    private boolean isExported;

    public Perl6ScopedDeclStubImpl(StubElement parent, String scope) {
        super(parent, Perl6ElementTypes.SCOPED_DECLARATION);
        this.scope = scope;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public boolean isExported() {
        return isExported;
    }
}
