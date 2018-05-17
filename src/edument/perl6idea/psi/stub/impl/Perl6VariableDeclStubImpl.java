package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.psi.stub.Perl6VariableDeclStub;

public class Perl6VariableDeclStubImpl extends StubBase<Perl6VariableDecl> implements Perl6VariableDeclStub {
    private String variableName;

    public Perl6VariableDeclStubImpl(StubElement stub, String name) {
        super(stub, Perl6ElementTypes.VARIABLE_DECLARATION);
        this.variableName = name;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }
}
