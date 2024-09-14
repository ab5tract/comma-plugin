package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.annotation.fix.FatarrowSimplificationFix;
import org.raku.comma.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NamedPairArgumentAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof RakuColonPair)
            checkColonPair((RakuColonPair)psiElement, annotationHolder);
        else if (psiElement instanceof RakuFatArrow)
            checkFatArrow((RakuFatArrow)psiElement, annotationHolder);
    }

    private static void checkFatArrow(RakuFatArrow arrow, AnnotationHolder annotationHolder) {
        String key = arrow.getKey();
        PsiElement value = arrow.getValue();
        processPair(value, key, arrow, annotationHolder);
    }

    private static void checkColonPair(RakuColonPair pair, AnnotationHolder annotationHolder) {
        String key = pair.getKey();
        if (key == null) return;
        PsiElement value = pair.getStatement();
        if (value == null) return;
        PsiElement child = value.getFirstChild();
        // Check if it is `()` form we can work with
        PsiElement parensExpr = PsiTreeUtil.getChildOfType(pair, RakuParenthesizedExpr.class);
        if (parensExpr == null) return;
        processPair(child, key, pair, annotationHolder);
    }

    public static String getSimplifiedText(PsiElement pair, String key, PsiElement element) {
        if (element instanceof RakuTypeName) {
            if (!PsiTreeUtil.isAncestor(pair, element, false))
                return null;
            String typeName = ((RakuTypeName)element).getTypeName();
            if (typeName.equals("True"))
                return key;
            if (typeName.equals("False"))
                return "!" + key;
        }
        if (element instanceof RakuVariable) {
            if (!PsiTreeUtil.isAncestor(pair, element, false))
                return null;
            String name = ((RakuVariable)element).getVariableName();
            if (name == null || name.length() < 2)
                return null;

            int prefixLength = RakuVariable.getTwigil(name) == ' ' ? 1 : 2;

            if (Objects.equals(key, name.substring(prefixLength)))
                return name;
        }
        return null;
    }

    private static void processPair(PsiElement element, String key, PsiElement pair, AnnotationHolder holder) {
        String simplifiedPair = getSimplifiedText(pair, key, element);
        if (simplifiedPair == null)
            return;
        holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Pair literal can be simplified")
          .range(pair).withFix(new FatarrowSimplificationFix(pair, simplifiedPair)).create();
    }
}
