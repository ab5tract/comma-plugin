package edument.perl6idea.cro.template.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import edument.perl6idea.cro.template.psi.CroTemplateIteration;
import edument.perl6idea.cro.template.psi.CroTemplateSeparator;
import org.jetbrains.annotations.NotNull;

public class MisplacedSeparatorAnnotation implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // Separator should be in an element sequence and then in an iteration
        // to be well-placed.
        if (!(element instanceof CroTemplateSeparator))
            return;
        PsiElement body = element.getParent();
        if (body != null)
            body = body.getParent();
        if (!(body instanceof CroTemplateIteration))
            holder.newAnnotation(HighlightSeverity.ERROR, "Separator may only occur directly in an iteration")
                    .range(element)
                    .create();
    }
}
