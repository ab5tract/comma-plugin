package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6ScopedDecl;

public interface Perl6ScopedDeclStub extends StubElement<Perl6ScopedDecl> {
    String getScope();
}
