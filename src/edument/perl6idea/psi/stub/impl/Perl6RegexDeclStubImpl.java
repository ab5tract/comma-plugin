package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6RegexDecl;
import edument.perl6idea.psi.stub.Perl6RegexDeclStub;

public class Perl6RegexDeclStubImpl extends StubBase<Perl6RegexDecl> implements Perl6RegexDeclStub {
    private String regexName;

    public Perl6RegexDeclStubImpl(StubElement stub, String name) {
        super(stub, Perl6ElementTypes.REGEX_DECLARATION);
        this.regexName = name;
    }

    @Override
    public String getRegexName() {
        return regexName;
    }
}
