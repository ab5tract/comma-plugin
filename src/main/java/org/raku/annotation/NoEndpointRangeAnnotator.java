package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.raku.annotation.fix.NoEndPointRangeFix;
import org.raku.psi.*;
import org.jetbrains.annotations.NotNull;

import static org.raku.parsing.RakuTokenTypes.UNV_WHITE_SPACE;

public class NoEndpointRangeAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuInfix)) return;
        if (!element.getText().equals("..")) return;
        PsiElement next = element.getNextSibling();
        while (next != null && next.getNode().getElementType() == UNV_WHITE_SPACE || next instanceof PsiWhiteSpace)
            next = next.getNextSibling();
        if (next instanceof RakuWhatever ||
            next instanceof RakuIntLiteral ||
            next instanceof RakuComplexLiteral ||
            next instanceof RakuNumLiteral ||
            next instanceof RakuRatLiteral ||
            next instanceof RakuVariable ||
            next instanceof RakuParenthesizedExpr ||
            next instanceof RakuInfixApplication ||
            next instanceof RakuStrLiteral ||
            next instanceof RakuPrefixApplication ||
            next instanceof RakuPostfixApplication)
            return;
        holder.newAnnotation(HighlightSeverity.ERROR, "The range operator must have a second argument")
            .range(element).withFix(new NoEndPointRangeFix(element.getTextOffset() + element.getTextLength())).create();

    }
}
