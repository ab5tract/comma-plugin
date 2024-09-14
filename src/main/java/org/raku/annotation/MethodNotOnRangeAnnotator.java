package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.raku.psi.RakuPrefix;
import org.raku.psi.RakuPrefixApplication;
import org.jetbrains.annotations.NotNull;

/* Detects ^5.foo, which is probably a mistake. */
public class MethodNotOnRangeAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder) {
        if (psiElement instanceof RakuPrefixApplication prefixApp) {
            PsiElement maybePrefix = prefixApp.getFirstChild();
            if (!(maybePrefix instanceof RakuPrefix))
                return;
            if (!maybePrefix.textMatches("^"))
                return;
            String rest = psiElement.getText().substring(1);
            if (rest.matches("^\\d+\\..+"))
                holder.newAnnotation(HighlightSeverity.WARNING, "Precedence of ^ is looser than method call; please parenthesize")
                    .range(psiElement).create();
        }
    }
}
