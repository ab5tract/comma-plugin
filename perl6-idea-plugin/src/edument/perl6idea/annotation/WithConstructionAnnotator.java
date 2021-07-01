package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import edument.perl6idea.annotation.fix.UseWithSyntaxFix;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WithConstructionAnnotator implements Annotator {
    public final static Set<String> terms = new HashSet<>();

    static {
        Collections.addAll(terms, "if", "elsif", "unless");
    }

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (!(psiElement instanceof P6Conditional)) {
            return;
        }

        P6Conditional conditional = (P6Conditional)psiElement;

        for (Perl6ConditionalBranch branch : conditional.getBranches()) {
            if (terms.contains(branch.term.getText()) && checkIfReplaceable((branch.condition))) {
                annotationHolder.newAnnotation(HighlightSeverity.WEAK_WARNING, psiElement instanceof Perl6IfStatement
                                                                               ? "'with' construction can be used instead"
                                                                               : "'without' construction can be used instead")
                    .range(new TextRange(branch.term.getTextOffset(), branch.condition.getTextOffset() + branch.condition.getTextLength()))
                    .withFix(new UseWithSyntaxFix(branch)).create();
            }
        }
    }

    private static boolean checkIfReplaceable(PsiElement condition) {
        if (!(condition instanceof Perl6PostfixApplication)) {
            return false;
        }

        PsiElement maybeMethodCall = condition.getLastChild();
        if (!(maybeMethodCall instanceof Perl6MethodCall)) {
            return false;
        }

        String methodName = ((Perl6MethodCall)maybeMethodCall).getCallName();
        return Objects.equals(methodName, ".defined");
    }
}
