package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.symbols.Perl6LexicalSymbolContributor;

public interface Perl6InfixApplication extends Perl6PsiElement, P6Extractable, Perl6LexicalSymbolContributor {
    PsiElement[] getOperands();
    String getOperator();
}
