package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.raku.comma.annotation.fix.RemoveInitializerFix;
import org.raku.comma.psi.*;
import org.jetbrains.annotations.NotNull;

public class EmptyInitializationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuVariableDecl decl))
            return;

        String[] names = decl.getVariableNames();
        if (names.length != 1)
            return;
        String name = names[0];
        char sigil = RakuVariable.getSigil(name);
        if (sigil != '@' && sigil != '%')
            return;

        boolean shouldAnnotate = false;
        PsiElement initializer = decl.getInitializer();

        if (sigil == '@' && initializer instanceof RakuArrayComposer) {
            shouldAnnotate = ((RakuArrayComposer)initializer).getElements().length == 0;
        } else if (initializer instanceof RakuParenthesizedExpr) {
            shouldAnnotate = ((RakuParenthesizedExpr)initializer).getElements().length == 0;
        } else if (sigil == '%' && initializer instanceof RakuBlockOrHash) {
            shouldAnnotate = ((RakuBlockOrHash)initializer).getElements().length == 0;
        }

        if (shouldAnnotate)
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING,
                                 String.format("Initialization of empty %s is redundant", sigil == '@' ? "Array" : "Hash"))
            .range(initializer).withFix(new RemoveInitializerFix(decl, name)).create();
    }
}
