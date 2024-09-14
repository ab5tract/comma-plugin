package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.raku.annotation.fix.GrepFirstFix;
import org.raku.psi.*;
import org.jetbrains.annotations.NotNull;

public class GrepFirstAnnotation implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuPostfixApplication))
            return;

        if (!(((RakuPostfixApplication)element).getOperand() instanceof RakuPostfixApplication innerApplication))
            return;
        RakuMethodCall grepCall;
        if (innerApplication != null && innerApplication.getPostfix() instanceof RakuMethodCall &&
            ((RakuMethodCall)innerApplication.getPostfix()).getCallName().equals(".grep")) {
            grepCall = (RakuMethodCall)innerApplication.getPostfix();
        }
        else {
            return;
        }

        PsiElement firstCall = ((RakuPostfixApplication)element).getPostfix();
        if (!(firstCall instanceof RakuMethodCall))
            return;

        if (!((RakuMethodCall)firstCall).getCallName().equals(".first"))
            return;

        PsiElement[] grepArgs = grepCall.getCallArguments();
        PsiElement[] firstArgs = ((RakuMethodCall)firstCall).getCallArguments();

        // Firstly, check if first call is arg-less, otherwise we don't
        // need to calculate grep at all
        if (firstArgs.length != 0 || grepArgs.length != 1)
            return;

        holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Can be simplified into a single first method call")
            .range(new TextRange(grepCall.getTextOffset(), element.getTextOffset() + element.getTextLength()))
            .withFix(new GrepFirstFix(grepCall, (RakuMethodCall)firstCall)).create();
    }
}
