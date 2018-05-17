package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.Stub;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6Enum;
import edument.perl6idea.psi.stub.Perl6EnumStub;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;

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

    @Override
    public String getScope() {
        return getParentStub() instanceof Perl6ScopedDeclStub
               ? ((Perl6ScopedDeclStub)getParentStub()).getScope()
               : "our";
    }

    @Override
    public String getGlobalName() {
        if (enumName == null)
            return null;
        String globalName = enumName;
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
        if (enumName == null)
            return null;
        String lexicalName = enumName;
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
