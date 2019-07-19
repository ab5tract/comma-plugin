package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.annotation.fix.GrepFirstFix;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GrepFirstAnnotation implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6MethodCall))
            return;

        // First call is "grep"
        if (!((Perl6MethodCall)element).getCallName().equals(".grep"))
            return;

        Perl6PostfixApplication upperApplication = PsiTreeUtil.getParentOfType(element, Perl6PostfixApplication.class);
        if (upperApplication == null)
            return;

        PsiElement nonWhitespacePostfix = upperApplication.skipWhitespacesForward();
        if (!(nonWhitespacePostfix instanceof Perl6MethodCall))
            return;

        if (!((Perl6MethodCall)nonWhitespacePostfix).getCallName().equals(".first"))
            return;

        PsiElement[] grepArgs = ((Perl6MethodCall)element).getCallArguments();
        PsiElement[] firstArgs = ((Perl6MethodCall)nonWhitespacePostfix).getCallArguments();

        // Firstly, check if first call is arg-less, otherwise we don't
        // need to calculate grep at all
        if (firstArgs.length != 0 || grepArgs.length != 1)
            return;

        holder.createWeakWarningAnnotation(
            new TextRange(element.getTextOffset(), nonWhitespacePostfix.getTextOffset() + nonWhitespacePostfix.getTextLength()),
            "Can be simplified into a single first method call")
            .registerFix(new GrepFirstFix((Perl6MethodCall)element, (Perl6MethodCall)nonWhitespacePostfix));
    }
}
