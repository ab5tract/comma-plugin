package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6Enum;
import edument.perl6idea.psi.stub.Perl6EnumStub;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;

import java.util.List;

public class Perl6EnumStubImpl extends StubBase<Perl6Enum> implements Perl6EnumStub {
    private final String enumName;
    private boolean isExported;
    private List<String> myEnumValues;

    public Perl6EnumStubImpl(StubElement stub, String name, boolean exported, List<String> enumValues) {
        super(stub, Perl6ElementTypes.ENUM);
        enumName = name;
        isExported = exported;
        myEnumValues = enumValues;
    }

    @Override
    public String getTypeName() {
        return enumName;
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
    public List<String> getEnumValues() {
        return myEnumValues;
    }
}
