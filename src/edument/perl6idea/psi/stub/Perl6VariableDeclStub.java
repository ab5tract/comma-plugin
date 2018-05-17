package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6VariableDecl;

public interface Perl6VariableDeclStub extends StubElement<Perl6VariableDecl> {
    String getVariableName();
    String getVariableKind();
}
