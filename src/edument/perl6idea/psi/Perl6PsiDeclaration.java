package edument.perl6idea.psi;

import com.intellij.psi.PsiNameIdentifierOwner;

public interface Perl6PsiDeclaration extends Perl6PsiElement, PsiNameIdentifierOwner {
    String getScope();
}
