package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6RoutineDecl;

public interface Perl6RoutineDeclStub extends StubElement<Perl6RoutineDecl> {
    String getRoutineName();
}
