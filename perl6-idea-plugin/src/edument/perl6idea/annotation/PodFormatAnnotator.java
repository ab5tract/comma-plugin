package edument.perl6idea.annotation;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import edument.perl6idea.highlighter.Perl6Highlighter;
import edument.perl6idea.psi.PodFormatted;
import org.jetbrains.annotations.NotNull;

public class PodFormatAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof PodFormatted) {
            PodFormatted formatted = (PodFormatted)psiElement;
            String formatCode = formatted.getFormatCode();
            switch (formatCode) {
                case "B": {
                    Annotation ann = annotationHolder.createAnnotation(HighlightSeverity.INFORMATION,
                            formatted.getFormattedTextRange(), null);
                    ann.setTextAttributes(Perl6Highlighter.POD_TEXT_BOLD);
                    break;
                }
                case "I": {
                    Annotation ann = annotationHolder.createAnnotation(HighlightSeverity.INFORMATION,
                            formatted.getFormattedTextRange(), null);
                    ann.setTextAttributes(Perl6Highlighter.POD_TEXT_ITALIC);
                    break;
                }
                case "U": {
                    Annotation ann = annotationHolder.createAnnotation(HighlightSeverity.INFORMATION,
                            formatted.getFormattedTextRange(), null);
                    ann.setTextAttributes(Perl6Highlighter.POD_TEXT_UNDERLINE);
                    break;
                }
            }
        }
    }
}
