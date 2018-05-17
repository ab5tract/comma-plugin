package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.Stub;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6Subset;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;
import edument.perl6idea.psi.stub.Perl6SubsetStub;

public class Perl6SubsetStubImpl extends StubBase<Perl6Subset> implements Perl6SubsetStub {
    private String subsetName;

    public Perl6SubsetStubImpl(StubElement stub, String name) {
        super(stub, Perl6ElementTypes.SUBSET);
        this.subsetName = name;
    }

    @Override
    public String getSubsetName() {
        return subsetName;
    }

    @Override
    public String getScope() {
        return getParentStub() instanceof Perl6ScopedDeclStub
               ? ((Perl6ScopedDeclStub)getParentStub()).getScope()
               : "our";
    }

    @Override
    public String getGlobalName() {
        if (subsetName == null)
            return null;
        String globalName = subsetName;
        Stub current = getParentStub();
        while (current != null) {
            if (current instanceof Perl6ScopedDeclStub)
                if (((Perl6ScopedDeclStub)current).getScope().equals("my"))
                    return null;
            if (current instanceof Perl6PackageDeclStub)
                globalName = ((Perl6PackageDeclStub)current).getPackageName() + "::" + globalName;
            current = current.getParentStub();
        }
        return globalName;
    }

    @Override
    public String getLexicalName() {
        if (subsetName == null)
            return null;
        String lexicalName = subsetName;
        Stub current = getParentStub();
        while (current != null) {
            if (current instanceof Perl6ScopedDeclStub) {
                if (((Perl6ScopedDeclStub)current).getScope().equals("my"))
                    return lexicalName;
                return null;
            }
            if (current instanceof Perl6PackageDeclStub)
                lexicalName = ((Perl6PackageDeclStub)current).getPackageName() + "::" + lexicalName;
            current = current.getParentStub();
        }
        return null;
    }
}
