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
            if (formatCode.equals("B")) {
                Annotation ann = annotationHolder.createAnnotation(HighlightSeverity.INFORMATION,
                    formatted.getFormattedTextRange(), null);
                ann.setTextAttributes(Perl6Highlighter.POD_TEXT_BOLD);
            }
            else if (formatCode.equals("I")) {
                Annotation ann = annotationHolder.createAnnotation(HighlightSeverity.INFORMATION,
                    formatted.getFormattedTextRange(), null);
                ann.setTextAttributes(Perl6Highlighter.POD_TEXT_ITALIC);
            }
            else if (formatCode.equals("U")) {
                Annotation ann = annotationHolder.createAnnotation(HighlightSeverity.INFORMATION,
                    formatted.getFormattedTextRange(), null);
                ann.setTextAttributes(Perl6Highlighter.POD_TEXT_UNDERLINE);
            }
        }
    }
}
