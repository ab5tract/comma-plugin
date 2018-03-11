package edument.perl6idea.psi;

public interface Perl6RegexDecl extends Perl6PsiElement, Perl6PsiScope, Perl6PsiDeclaration {
    String getRegexKind();
    String getRegexName();
}
