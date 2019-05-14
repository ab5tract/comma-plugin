package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public interface Perl6SubCall extends Perl6PsiElement, PsiNamedElement, P6Extractable {
    String getSubCallName();
    PsiElement[] getSubCallArguments();
}
