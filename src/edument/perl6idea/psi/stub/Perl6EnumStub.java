package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6Enum;

public interface Perl6EnumStub extends StubElement<Perl6Enum> {
    String getEnumName();
    String getScope();
}
