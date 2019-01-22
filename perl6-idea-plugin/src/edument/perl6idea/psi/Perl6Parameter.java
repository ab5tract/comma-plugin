package edument.perl6idea.psi;

public interface Perl6Parameter extends Perl6PsiElement, Perl6PsiDeclaration {
    String summary();
    String getVariableName();
}
