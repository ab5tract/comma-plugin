package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;

public interface PodFormatted extends PodElement {
    String getFormatCode();
    TextRange getFormattedTextRange();
    String renderPod();
}
