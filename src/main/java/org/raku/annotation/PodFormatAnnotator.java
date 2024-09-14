package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.raku.highlighter.RakuHighlighter;
import org.raku.psi.PodFormatted;
import org.jetbrains.annotations.NotNull;

public class PodFormatAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof PodFormatted formatted) {
            String formatCode = formatted.getFormatCode();
            switch (formatCode) {
                case "B": {
                    annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RakuHighlighter.POD_TEXT_BOLD)
                        .range(formatted.getFormattedTextRange()).create();
                    break;
                }
                case "I": {
                    annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RakuHighlighter.POD_TEXT_ITALIC)
                        .range(formatted.getFormattedTextRange()).create();
                    break;
                }
                case "U": {
                    annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RakuHighlighter.POD_TEXT_UNDERLINE)
                        .range(formatted.getFormattedTextRange()).create();
                    break;
                }
            }
        }
    }
}
