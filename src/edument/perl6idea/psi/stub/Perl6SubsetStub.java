package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6Subset;

public interface Perl6SubsetStub extends StubElement<Perl6Subset> {
    String getSubsetName();
    String getScope();
}
