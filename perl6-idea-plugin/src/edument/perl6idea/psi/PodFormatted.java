package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;

public interface PodFormatted {
    String getFormatCode();
    TextRange getFormattedTextRange();
}
