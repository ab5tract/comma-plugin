package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.annotation.fix.PairSimplificationFix;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NamedPairArgumentAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof Perl6ColonPair)
            checkColonPair((Perl6ColonPair)psiElement, annotationHolder);
        else if (psiElement instanceof Perl6FatArrow)
            checkFatArrow((Perl6FatArrow)psiElement, annotationHolder);
    }

    private static void checkFatArrow(Perl6FatArrow arrow, AnnotationHolder annotationHolder) {
        String key = arrow.getKey();
        PsiElement value = arrow.getValue();
        processPair(value, key, arrow, annotationHolder);
    }

    private static void checkColonPair(Perl6ColonPair pair, AnnotationHolder annotationHolder) {
        String key = pair.getKey();
        if (key == null) return;
        Perl6Statement value = pair.getStatement();
        if (value == null) return;
        PsiElement child = value.getFirstChild();
        // Check if it is `()` form we can work with
        PsiElement parensExpr = PsiTreeUtil.getChildOfType(pair, Perl6ParenthesizedExpr.class);
        if (parensExpr == null) return;
        processPair(child, key, pair, annotationHolder);
    }

    private static void processPair(PsiElement element, String key, PsiElement pair, AnnotationHolder holder) {
        if (!(element instanceof Perl6Variable))
            return;

        String name = ((Perl6Variable)element).getVariableName();
        if (name == null)
            return;

        int prefixLength = Perl6Variable.getTwigil(name) == ' ' ? 1 : 2;

        if (Objects.equals(key, name.substring(prefixLength))) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Pair literal can be simplified")
                .range(pair).withFix(new PairSimplificationFix(pair, (Perl6Variable)element)).create();
        }
    }
}
