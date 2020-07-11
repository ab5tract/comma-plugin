package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import edument.perl6idea.annotation.fix.NullRegexFix;
import edument.perl6idea.psi.Perl6Regex;
import org.jetbrains.annotations.NotNull;

public class NullRegexAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6Regex))
            return;
        if (element.getText().isEmpty())
            holder.newAnnotation(HighlightSeverity.ERROR, "Empty regex is not allowed")
                .range(element.getParent()).withFix(new NullRegexFix()).create();
    }
}
