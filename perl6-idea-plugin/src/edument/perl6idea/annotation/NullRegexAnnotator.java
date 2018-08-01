package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Regex;
import org.jetbrains.annotations.NotNull;

public class NullRegexAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6Regex))
            return;
        if (element.getText().isEmpty())
            holder.createErrorAnnotation(element.getParent(), "Empty regex is not allowed");
    }
}
