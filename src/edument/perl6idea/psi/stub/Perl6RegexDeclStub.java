package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6RegexDecl;

public interface Perl6RegexDeclStub extends StubElement<Perl6RegexDecl> {
    String getRegexName();
    String getScope();
}
