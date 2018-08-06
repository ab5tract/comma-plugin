package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6Signature;
import org.jetbrains.annotations.NotNull;

public class SignatureAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6Signature))
            return;

        SignatureState state = null;
        PsiElement[] params = element.getChildren();
        if (params.length == 0) return;

        boolean isFirst = true;

        for (PsiElement psiElement : params) {
            if (!(psiElement instanceof Perl6Parameter)) continue;
            Perl6Parameter parameter = (Perl6Parameter)psiElement;

            String summary = parameter.summary();
            if (isFirst) {
                state = isRequired(summary) ? SignatureState.REQUIRED :
                        isOptional(summary) ? SignatureState.OPTIONAL :
                        SignatureState.VARIADIC;
                isFirst = false;
            }

            if (state == SignatureState.REQUIRED && isOptional(summary))
                state = SignatureState.OPTIONAL;
            else if (isVariadic(summary) && (state == SignatureState.REQUIRED || state == SignatureState.OPTIONAL))
                state = SignatureState.VARIADIC;

            if (state == SignatureState.OPTIONAL && isRequired(summary)) {
                holder.createErrorAnnotation(
                    parameter, String.format("Cannot put positional parameter %s after a named parameter", parameter.getVariableName()));
                break;
            } else if (state == SignatureState.VARIADIC && isRequired(summary)) {
                holder.createErrorAnnotation(
                    parameter, String.format("Cannot put positional parameter %s after a variadic parameter", parameter.getVariableName()));
                break;
            } else if (state == SignatureState.VARIADIC && isOptional(summary)) {
                holder.createErrorAnnotation(
                    parameter, String.format("Cannot put optional parameter %s after a variadic parameter", parameter.getVariableName()));
            }
        }
    }

    private static boolean isRequired(String summary) {
        return summary.equals("$") || summary.equals("@") ||
               summary.equals("%") || summary.equals("&");
    }

    private static boolean isOptional(String summary) {
        return summary.endsWith("?") || summary.startsWith(":");
    }

    private static boolean isVariadic(String summary) {
        return summary.startsWith("*") || summary.startsWith("|") ||
               summary.startsWith("+");
    }

    enum SignatureState {
        REQUIRED, OPTIONAL, VARIADIC
    }
}
