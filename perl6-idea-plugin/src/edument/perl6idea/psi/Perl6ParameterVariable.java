package edument.perl6idea.psi;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.meta.PsiMetaOwner;

public interface Perl6ParameterVariable extends Perl6PsiDeclaration, PsiNamedElement, PsiMetaOwner {
    String summary();
}
