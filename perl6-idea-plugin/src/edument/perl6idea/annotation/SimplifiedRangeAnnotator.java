package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.intention.RangeIntentionFix;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static edument.perl6idea.parsing.Perl6TokenTypes.*;

public class SimplifiedRangeAnnotator implements Annotator {
    static final Set<String> OPS = new HashSet<>(Arrays.asList("..", "..^"));

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element.getNode().getElementType() == INFIX))
            return;

        Perl6InfixApplication application = PsiTreeUtil.getParentOfType(element, Perl6InfixApplication.class);
        if (application == null)
            return;

        PsiElement infix = PsiTreeUtil.getChildOfType(application, Perl6Infix.class);
        if (infix == null) return;
        String op = infix.getText();
        if (!OPS.contains(op)) return;
        // Get and check possible siblings
        PsiElement rangeEnd = infix.getNextSibling();
        while (rangeEnd != null) {
            if (rangeEnd instanceof Perl6IntLiteral || rangeEnd instanceof Perl6Variable ||
                rangeEnd instanceof Perl6InfixApplication || rangeEnd instanceof Perl6ParenthesizedExpr)
                break;
            rangeEnd = rangeEnd.getNextSibling();
        }

        PsiElement rangeStart = PsiTreeUtil.getPrevSiblingOfType(infix, Perl6IntLiteral.class);
        Perl6InfixApplication infixInParens = null;
        if (rangeEnd instanceof Perl6ParenthesizedExpr)
            infixInParens = PsiTreeUtil.findChildOfType(rangeEnd, Perl6InfixApplication.class);
        boolean shouldAnnotate = rangeStart != null && rangeStart.getText().equals("0") && // Basic condition
               // Int condition
               (rangeEnd instanceof Perl6IntLiteral && (infix.getText().equals("..") || infix.getText().equals("..^")) ||
                // Var condition
                rangeEnd instanceof Perl6Variable && infix.getText().equals("..^") ||
                // Infix, possible `0..$n-1`
                rangeEnd instanceof Perl6InfixApplication && infix.getText().equals("..") ||
                // Parens, possible ..($n-1)
                rangeEnd instanceof Perl6ParenthesizedExpr && infix.getText().equals("..") &&
                ( // inner parens expr
                  infixInParens != null && checkInfix(infixInParens.getChildren())
                )
               );
        if (!shouldAnnotate)
            return;

        holder.createWeakWarningAnnotation(
            new TextRange(rangeStart.getTextOffset(), rangeEnd.getTextOffset() + rangeEnd.getTextLength()), "Range can be simplified")
            .registerFix(new RangeIntentionFix());
    }

    private static boolean checkInfix(PsiElement[] children) {
        return children.length == 3 &&
               children[0] instanceof Perl6Variable &&
               children[1] instanceof Perl6Infix && children[1].getText().equals("-") &&
               children[2] instanceof Perl6IntLiteral && children[2].getText().equals("1");
    }
}
