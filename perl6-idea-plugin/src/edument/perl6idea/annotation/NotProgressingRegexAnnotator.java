package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6RegexAtom;
import edument.perl6idea.psi.Perl6RegexPsiElement;
import edument.perl6idea.psi.Perl6RegexQuantifier;
import org.jetbrains.annotations.NotNull;

public class NotProgressingRegexAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // Only want quantified regex atoms.
        if (!(element instanceof Perl6RegexAtom))
            return;
        Perl6RegexQuantifier quantifier = PsiTreeUtil.getChildOfType(element, Perl6RegexQuantifier.class);
        if (quantifier == null)
            return;

        // Should be + or * quantifier.
        String quantText = quantifier.getFirstChild().getText();
        if (!(quantText.equals("+") || quantText.equals("*")))
            return;

        // See if the atom might match nothing.
        boolean maybeProblematic = false;
        PsiElement child = element.getFirstChild();
        if (child instanceof Perl6RegexPsiElement) {
            if (((Perl6RegexPsiElement)child).mightMatchZeroWidth())
                maybeProblematic = true;
        }

        // Annotate lack of progress.
        if (maybeProblematic)
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Quantified expression may not progress, leading to a hang")
                    .range(quantifier)
                    .create();
    }
}
