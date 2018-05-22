package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6NeedStatement;
import edument.perl6idea.psi.stub.Perl6NeedStatementStub;

import java.util.List;

public class Perl6NeedStatementStubImpl extends StubBase<Perl6NeedStatement> implements Perl6NeedStatementStub {
    private List<String> moduleNames;

    public Perl6NeedStatementStubImpl(StubElement parent, List<String> moduleNames) {
        super(parent, Perl6ElementTypes.NEED_STATEMENT);
        this.moduleNames = moduleNames;
    }

    @Override
    public List<String> getModuleNames() {
        return moduleNames;
    }
}
