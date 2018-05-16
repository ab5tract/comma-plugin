package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6Constant;

public interface Perl6ConstantStub extends StubElement<Perl6Constant> {
    String getConstantName();
}
