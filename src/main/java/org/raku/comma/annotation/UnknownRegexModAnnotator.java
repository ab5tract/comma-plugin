package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.raku.comma.parsing.RakuTokenTypes;
import org.raku.comma.psi.RakuRegexModInternal;
import org.jetbrains.annotations.NotNull;

public class UnknownRegexModAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof RakuRegexModInternal) {
            if (element.getNode().findChildByType(RakuTokenTypes.REGEX_MOD_UNKNOWN) != null) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unrecognized regex modifier")
                    .create();
            }
        }
    }
}
