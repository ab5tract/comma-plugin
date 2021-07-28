package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6RegexDecl;
import edument.perl6idea.psi.stub.Perl6RegexDeclStub;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;

public class Perl6RegexDeclStubImpl extends StubBase<Perl6RegexDecl> implements Perl6RegexDeclStub {
    private final String regexName;
    private final boolean isExported;

    public Perl6RegexDeclStubImpl(StubElement stub, String name, boolean exported) {
        super(stub, Perl6ElementTypes.REGEX_DECLARATION);
        this.regexName = name;
        isExported = exported;
    }

    @Override
    public String getRegexName() {
        return regexName;
    }

    @Override
    public String getScope() {
        return getParentStub() instanceof Perl6ScopedDeclStub
               ? ((Perl6ScopedDeclStub)getParentStub()).getScope()
               : "has";
    }

    @Override
    public boolean isExported() {
        return isExported;
    }
}
