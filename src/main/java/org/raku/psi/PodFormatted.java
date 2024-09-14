package org.raku.psi;

import com.intellij.openapi.util.TextRange;
import org.raku.pod.PodDomNode;

public interface PodFormatted extends PodElement {
    String getFormatCode();
    TextRange getFormattedTextRange();
    PodDomNode buildPodDom();
}
