package edument.perl6idea.psi;

public interface Perl6Signature extends Perl6PsiElement {
    String summary(String type);
    Perl6Parameter[] getParameters();
}
