package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6Subset;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;
import edument.perl6idea.psi.stub.Perl6SubsetStub;

public class Perl6SubsetStubImpl extends StubBase<Perl6Subset> implements Perl6SubsetStub {
    private String subsetName;
    private boolean isExported;

    public Perl6SubsetStubImpl(StubElement stub, String name) {
        super(stub, Perl6ElementTypes.SUBSET);
        this.subsetName = name;
    }

    @Override
    public String getTypeName() {
        return subsetName;
    }

    @Override
    public String getScope() {
        return getParentStub() instanceof Perl6ScopedDeclStub
               ? ((Perl6ScopedDeclStub)getParentStub()).getScope()
               : "our";
    }

    @Override
    public boolean isExported() {
        return isExported;
    }
}
