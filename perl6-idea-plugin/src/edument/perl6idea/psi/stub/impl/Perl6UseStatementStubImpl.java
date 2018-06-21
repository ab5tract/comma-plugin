package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6UseStatement;
import edument.perl6idea.psi.stub.Perl6UseStatementStub;

public class Perl6UseStatementStubImpl extends StubBase<Perl6UseStatement> implements Perl6UseStatementStub {
    private String moduleName;

    public Perl6UseStatementStubImpl(StubElement parent, String moduleName) {
        super(parent, Perl6ElementTypes.USE_STATEMENT);
        this.moduleName = moduleName;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }
}
