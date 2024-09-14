package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.intention.RangeIntentionFix;
import org.raku.comma.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SimplifiedRangeAnnotator implements Annotator {
    static final Set<String> OPS = new HashSet<>(Arrays.asList("..", "..^"));

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuInfixApplication))
            return;

        PsiElement infix = PsiTreeUtil.getChildOfType(element, RakuInfix.class);
        if (infix == null) return;
        String op = infix.getText();
        if (!OPS.contains(op)) return;
        // Get and check possible siblings
        PsiElement rangeEnd = infix.getNextSibling();
        while (rangeEnd != null) {
            if (rangeEnd instanceof RakuIntLiteral || rangeEnd instanceof RakuVariable ||
                rangeEnd instanceof RakuInfixApplication || rangeEnd instanceof RakuParenthesizedExpr)
                break;
            rangeEnd = rangeEnd.getNextSibling();
        }

        PsiElement rangeStart = PsiTreeUtil.getPrevSiblingOfType(infix, RakuIntLiteral.class);
        RakuInfixApplication infixInParens = null;
        if (rangeEnd instanceof RakuParenthesizedExpr)
            infixInParens = PsiTreeUtil.findChildOfType(rangeEnd, RakuInfixApplication.class);
        boolean shouldAnnotate = rangeStart != null && rangeStart.getText().equals("0") && // Basic condition
               // Int condition
               (rangeEnd instanceof RakuIntLiteral && (infix.getText().equals("..") || infix.getText().equals("..^")) ||
                // Var condition
                rangeEnd instanceof RakuVariable && infix.getText().equals("..^") ||
                // Infix, possible `0..$n-1`
                rangeEnd instanceof RakuInfixApplication && infix.getText().equals("..") &&
                PsiTreeUtil.findChildOfType(rangeEnd, RakuWhatever.class) == null ||
                // Parens, possible ..($n-1)
                rangeEnd instanceof RakuParenthesizedExpr && infix.getText().equals("..") &&
                ( // inner parens expr
                  infixInParens != null && checkInfix(infixInParens.getChildren())
                )
               );
        if (!shouldAnnotate)
            return;

        holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Range can be simplified")
            .range(new TextRange(rangeStart.getTextOffset(), rangeEnd.getTextOffset() + rangeEnd.getTextLength()))
            .withFix(new RangeIntentionFix()).create();
    }

    private static boolean checkInfix(PsiElement[] children) {
        return children.length == 3 &&
               children[0] instanceof RakuVariable &&
               children[1] instanceof RakuInfix && children[1].getText().equals("-") &&
               children[2] instanceof RakuIntLiteral && children[2].getText().equals("1");
    }
}
