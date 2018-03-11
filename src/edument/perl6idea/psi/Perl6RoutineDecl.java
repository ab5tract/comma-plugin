package edument.perl6idea.psi;

public interface Perl6RoutineDecl extends Perl6PsiElement, Perl6PsiScope, Perl6PsiDeclaration {
    String getRoutineKind();
    String getRoutineName();
}
