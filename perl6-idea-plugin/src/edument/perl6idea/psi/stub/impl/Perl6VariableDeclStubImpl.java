package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;
import edument.perl6idea.psi.stub.Perl6VariableDeclStub;

public class Perl6VariableDeclStubImpl extends StubBase<Perl6VariableDecl> implements Perl6VariableDeclStub {
    private String[] variableNames;
    private String variableType;
    private boolean isExported;

    public Perl6VariableDeclStubImpl(StubElement stub, String[] names, String type, boolean exported) {
        super(stub, Perl6ElementTypes.VARIABLE_DECLARATION);
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
        return getParentStub() instanceof Perl6ScopedDeclStub
               ? ((Perl6ScopedDeclStub)getParentStub()).getScope()
               : ""; /* Shouldn't ever happen */
    }

    @Override
    public boolean isExported() {
        return isExported;
    }
}
