package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6React;
import edument.perl6idea.psi.Perl6Supply;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.STATEMENT_CONTROL;

public class WheneverOutsideOfReactAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!element.getNode().getElementType().equals(STATEMENT_CONTROL) ||
            !element.getText().equals("whenever"))
            return;
        PsiElement react = PsiTreeUtil.findFirstParent(element, true, p -> p instanceof Perl6React || p instanceof Perl6Supply);
        if (react == null)
            holder.newAnnotation(HighlightSeverity.ERROR, "A whenever must be within a supply or react block")
                .range(element).create();
    }

}
