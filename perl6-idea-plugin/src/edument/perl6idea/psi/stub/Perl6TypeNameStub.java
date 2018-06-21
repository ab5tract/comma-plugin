package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6TypeName;

public interface Perl6TypeNameStub extends StubElement<Perl6TypeName> {
    String getTypeName();
}
