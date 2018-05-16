package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6Enum;
import edument.perl6idea.psi.stub.Perl6EnumStub;

public class Perl6EnumStubImpl extends StubBase<Perl6Enum> implements Perl6EnumStub {
    private final String enumName;

    public Perl6EnumStubImpl(StubElement stub, String name) {
        super(stub, Perl6ElementTypes.ENUM);
        this.enumName = name;
    }

    @Override
    public String getEnumName() {
        return enumName;
    }
}
