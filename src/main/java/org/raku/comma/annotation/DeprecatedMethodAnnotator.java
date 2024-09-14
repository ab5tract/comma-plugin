package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.raku.comma.psi.RakuDeprecatable;
import org.raku.comma.psi.RakuMethodCall;
import org.jetbrains.annotations.NotNull;

public class DeprecatedMethodAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof RakuMethodCall) {
            PsiReference reference = element.getReference();
            if (reference == null)
                return;
            PsiElement resolution = reference.resolve();
            if (resolution instanceof RakuDeprecatable && ((RakuDeprecatable)resolution).isDeprecated()) {
                PsiElement methodName = ((RakuMethodCall)element).getSimpleName();
                if (methodName == null || methodName.getTextLength() == 0)
                    return;
                String deprecationMessage = ((RakuDeprecatable)resolution).getDeprecationMessage();
                String message = methodName.getText() + " is deprecated" + (deprecationMessage != null ? "; use " + deprecationMessage : "");
                holder.newAnnotation(HighlightSeverity.WARNING, message)
                    .range(methodName)
                    .textAttributes(CodeInsightColors.DEPRECATED_ATTRIBUTES)
                    .create();
            }
        }
    }
}
