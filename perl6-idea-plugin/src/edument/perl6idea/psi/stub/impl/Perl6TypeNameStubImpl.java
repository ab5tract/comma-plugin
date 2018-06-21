package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6TypeName;
import edument.perl6idea.psi.stub.Perl6TypeNameStub;

public class Perl6TypeNameStubImpl extends StubBase<Perl6TypeName> implements Perl6TypeNameStub {
    private final String typeName;

    public Perl6TypeNameStubImpl(StubElement parent, String name) {
        super(parent, Perl6ElementTypes.TYPE_NAME);
        this.typeName = name;
    }


    @Override
    public String getTypeName() {
        return typeName;
    }
}
