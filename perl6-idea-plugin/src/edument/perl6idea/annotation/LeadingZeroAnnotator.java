package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6IntLiteral;
import org.jetbrains.annotations.NotNull;

public class LeadingZeroAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof Perl6IntLiteral) {
            String literal = psiElement.getText();
            if (literal.length() > 1 && literal.startsWith("0") && Character.isDigit(literal.charAt(1)))
                annotationHolder.createWarningAnnotation(psiElement,
                        "Leading 0 does not indicate octal in Perl 6; use 0o" + literal.substring(1));
        }
    }
}
