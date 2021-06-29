package edument.perl6idea.psi;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.meta.PsiMetaOwner;
import edument.perl6idea.psi.symbols.Perl6LexicalSymbolContributor;

public interface Perl6ParameterVariable extends Perl6PsiDeclaration, PsiNamedElement, PsiMetaOwner,
                                                Perl6LexicalSymbolContributor {
    String summary(boolean includeName);
}
