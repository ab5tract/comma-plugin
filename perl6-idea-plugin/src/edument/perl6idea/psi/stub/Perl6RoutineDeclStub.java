package edument.perl6idea.psi.stub;

import edument.perl6idea.psi.Perl6RoutineDecl;

public interface Perl6RoutineDeclStub extends Perl6DeclStub<Perl6RoutineDecl> {
    String getRoutineKind();
    String getRoutineName();
    boolean isPrivate();
    String getMultiness();
}
