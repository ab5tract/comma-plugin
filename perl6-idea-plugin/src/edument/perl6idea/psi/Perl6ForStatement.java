package edument.perl6idea.psi;

import edument.perl6idea.psi.type.Perl6Type;

public interface Perl6ForStatement extends Perl6PsiElement, P6Extractable, P6Control, P6Topicalizer {
    Perl6PsiElement getSource();
    Perl6Type inferLoopParameterType(int index);
}
