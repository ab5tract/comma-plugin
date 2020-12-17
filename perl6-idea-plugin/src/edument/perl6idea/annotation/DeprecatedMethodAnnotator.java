package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6Deprecatable;
import edument.perl6idea.psi.Perl6MethodCall;
import org.jetbrains.annotations.NotNull;

public class DeprecatedMethodAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof Perl6MethodCall) {
            PsiReference reference = element.getReference();
            if (reference == null)
                return;
            PsiElement resolution = reference.resolve();
            if (resolution instanceof Perl6Deprecatable && ((Perl6Deprecatable)resolution).isDeprecated()) {
                PsiElement methodName = ((Perl6MethodCall)element).getSimpleName();
                if (methodName == null || methodName.getTextLength() == 0)
                    return;
                String deprecationMessage = ((Perl6Deprecatable)resolution).getDeprecationMessage();
                String message = methodName.getText() + " is deprecated" + (deprecationMessage != null ? "; use " + deprecationMessage : "");
                holder.newAnnotation(HighlightSeverity.WARNING, message)
                    .range(methodName)
                    .textAttributes(CodeInsightColors.DEPRECATED_ATTRIBUTES)
                    .create();
            }
        }
    }
}
