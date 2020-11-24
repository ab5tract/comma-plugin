package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import edument.perl6idea.annotation.fix.GrepFirstFix;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

public class GrepFirstAnnotation implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6PostfixApplication))
            return;

        if (!(((Perl6PostfixApplication)element).getOperand() instanceof Perl6PostfixApplication))
            return;
        Perl6PostfixApplication innerApplication = (Perl6PostfixApplication)((Perl6PostfixApplication)element).getOperand();
        Perl6MethodCall grepCall;
        if (innerApplication != null && innerApplication.getPostfix() instanceof Perl6MethodCall &&
            ((Perl6MethodCall)innerApplication.getPostfix()).getCallName().equals(".grep")) {
            grepCall = (Perl6MethodCall)innerApplication.getPostfix();
        }
        else {
            return;
        }

        PsiElement firstCall = ((Perl6PostfixApplication)element).getPostfix();
        if (!(firstCall instanceof Perl6MethodCall))
            return;

        if (!((Perl6MethodCall)firstCall).getCallName().equals(".first"))
            return;

        PsiElement[] grepArgs = grepCall.getCallArguments();
        PsiElement[] firstArgs = ((Perl6MethodCall)firstCall).getCallArguments();

        // Firstly, check if first call is arg-less, otherwise we don't
        // need to calculate grep at all
        if (firstArgs.length != 0 || grepArgs.length != 1)
            return;

        holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Can be simplified into a single first method call")
            .range(new TextRange(grepCall.getTextOffset(), element.getTextOffset() + element.getTextLength()))
            .withFix(new GrepFirstFix(grepCall, (Perl6MethodCall)firstCall)).create();
    }
}
