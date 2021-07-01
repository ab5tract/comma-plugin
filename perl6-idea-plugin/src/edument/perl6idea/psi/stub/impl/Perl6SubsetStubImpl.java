package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6Subset;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;
import edument.perl6idea.psi.stub.Perl6SubsetStub;

public class Perl6SubsetStubImpl extends StubBase<Perl6Subset> implements Perl6SubsetStub {
    private final String subsetName;
    private final boolean isExported;
    private final String baseTypeName;

    public Perl6SubsetStubImpl(StubElement stub, String name, boolean exported, String typeName) {
        super(stub, Perl6ElementTypes.SUBSET);
        subsetName = name;
        isExported = exported;
        baseTypeName = typeName;
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

    @Override
    public String getSubsetBaseTypeName() {
        return baseTypeName;
    }
}
