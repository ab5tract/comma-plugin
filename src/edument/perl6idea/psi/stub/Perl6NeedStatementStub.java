package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6NeedStatement;

import java.util.List;

public interface Perl6NeedStatementStub extends StubElement<Perl6NeedStatement> {
    List<String> getModuleNames();
}
