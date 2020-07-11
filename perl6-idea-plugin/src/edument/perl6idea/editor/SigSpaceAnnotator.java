package edument.perl6idea.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import edument.perl6idea.highlighter.Perl6Highlighter;
import edument.perl6idea.psi.Perl6RegexSigspace;
import org.jetbrains.annotations.NotNull;

public class SigSpaceAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof Perl6RegexSigspace && psiElement.getTextLength() >= 1) {
            annotationHolder.newAnnotation(HighlightSeverity.INFORMATION, "Implicit <.ws> call")
                .range(psiElement).textAttributes(Perl6Highlighter.REGEX_SIG_SPACE).create();
        }
    }
}
