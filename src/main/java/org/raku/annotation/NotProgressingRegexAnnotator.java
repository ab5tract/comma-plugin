package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuRegexAtom;
import org.raku.psi.RakuRegexPsiElement;
import org.raku.psi.RakuRegexQuantifier;
import org.jetbrains.annotations.NotNull;

public class NotProgressingRegexAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // Only want quantified regex atoms.
        if (!(element instanceof RakuRegexAtom))
            return;
        RakuRegexQuantifier quantifier = PsiTreeUtil.getChildOfType(element, RakuRegexQuantifier.class);
        if (quantifier == null)
            return;

        // Should be + or * quantifier.
        String quantText = quantifier.getFirstChild().getText();
        if (!(quantText.equals("+") || quantText.equals("*")))
            return;

        // See if the atom might match nothing.
        boolean maybeProblematic = false;
        PsiElement child = element.getFirstChild();
        if (child instanceof RakuRegexPsiElement) {
            if (((RakuRegexPsiElement)child).mightMatchZeroWidth())
                maybeProblematic = true;
        }

        // Annotate lack of progress.
        if (maybeProblematic)
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Quantified expression may not progress, leading to a hang")
                    .range(quantifier)
                    .create();
    }
}
