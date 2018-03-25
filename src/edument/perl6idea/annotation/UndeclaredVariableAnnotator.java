package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.Perl6VariableReference;
import org.jetbrains.annotations.NotNull;

public class UndeclaredVariableAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6Variable)) return;
        final Perl6Variable ref = (Perl6Variable) element;
        if ((ref.getTwigil() == ' ' || ref.getTwigil() == '!') && !(ref.getVariableName().contains("::"))
                && !(ref.getVariableName().contains(":[")))
            if (new Perl6VariableReference(ref).resolve() == null)
                holder.createErrorAnnotation(element,
                        String.format("Variable %s is not declared", ref.getVariableName()));
    }
}
