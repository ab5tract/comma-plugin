package org.raku.comma.psi;

import com.intellij.openapi.util.TextRange;
import org.raku.comma.pod.PodDomNode;

public interface PodFormatted extends PodElement {
    String getFormatCode();
    TextRange getFormattedTextRange();
    PodDomNode buildPodDom();
}
