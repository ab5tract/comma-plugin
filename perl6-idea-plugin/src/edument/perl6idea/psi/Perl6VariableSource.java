package edument.perl6idea.psi;

public interface Perl6VariableSource extends Perl6PsiElement, Perl6PsiDeclaration {
    String[] getVariableNames();
    Perl6Variable[] getVariables();
}
