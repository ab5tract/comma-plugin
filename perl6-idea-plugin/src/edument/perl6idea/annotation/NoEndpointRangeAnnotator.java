package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import edument.perl6idea.annotation.fix.NoEndPointRangeFix;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class NoEndpointRangeAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6Infix)) return;
        if (!element.getText().equals("..")) return;
        PsiElement next = element.getNextSibling();
        while (next != null && next.getNode().getElementType() == UNV_WHITE_SPACE || next instanceof PsiWhiteSpace)
            next = next.getNextSibling();
        if (next instanceof Perl6Whatever ||
            next instanceof Perl6IntLiteral ||
            next instanceof Perl6ComplexLiteral ||
            next instanceof Perl6NumLiteral ||
            next instanceof Perl6RatLiteral ||
            next instanceof Perl6Variable ||
            next instanceof Perl6ParenthesizedExpr ||
            next instanceof Perl6InfixApplication ||
            next instanceof Perl6StrLiteral ||
            next instanceof Perl6PrefixApplication ||
            next instanceof Perl6PostfixApplication)
            return;
        holder.createErrorAnnotation(element, "The range operator must have a second argument")
              .registerFix(new NoEndPointRangeFix(element.getTextOffset() + element.getTextLength()));

    }
}
