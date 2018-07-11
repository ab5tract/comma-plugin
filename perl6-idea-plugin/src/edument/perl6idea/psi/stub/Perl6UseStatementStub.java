package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6UseStatement;

public interface Perl6UseStatementStub extends StubElement<Perl6UseStatement> {
    String getModuleName();
}
