package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.raku.comma.annotation.fix.NullRegexFix;
import org.raku.comma.psi.RakuRegexDriver;
import org.jetbrains.annotations.NotNull;

public class NullRegexAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuRegexDriver))
            return;
        if (element.getText().isEmpty())
            holder.newAnnotation(HighlightSeverity.ERROR, "Empty regex is not allowed")
                .range(element).withFix(new NullRegexFix()).create();
    }
}
