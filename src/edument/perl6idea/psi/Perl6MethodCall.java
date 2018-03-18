package edument.perl6idea.psi;

public interface Perl6MethodCall extends Perl6PsiElement {
    String getCallName();
    String getCallOperator();
    int getNameStartOffset();
    int getNameEndOffset();
}
