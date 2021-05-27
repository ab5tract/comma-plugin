package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import edument.perl6idea.pod.PodDomNode;

public interface PodFormatted extends PodElement {
    String getFormatCode();
    TextRange getFormattedTextRange();
    PodDomNode buildPodDom();
}
