package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;

public interface PodFormatted extends Perl6PsiElement {
    String getFormatCode();
    TextRange getFormattedTextRange();
}
