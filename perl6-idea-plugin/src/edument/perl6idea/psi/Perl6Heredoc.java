package edument.perl6idea.psi;

import com.intellij.psi.PsiLanguageInjectionHost;

public interface Perl6Heredoc extends Perl6PsiElement, PsiLanguageInjectionHost {
    String getStringText();
    int getIndentation();
}
