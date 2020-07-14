package edument.perl6idea.psi;

import com.intellij.psi.PsiLanguageInjectionHost;

public interface Perl6StrLiteral extends Perl6PsiElement, P6Extractable, PsiLanguageInjectionHost {
    String getStringText();
}
