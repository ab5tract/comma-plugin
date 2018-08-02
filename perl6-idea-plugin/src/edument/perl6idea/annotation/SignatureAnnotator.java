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

            if (isFirst) {
                state = isRequired(parameter.summary()) ? SignatureState.REQUIRED :
                        isOptional(parameter.summary()) ? SignatureState.OPTIONAL :
                        SignatureState.VARIADIC;
                isFirst = false;
            }

            if (state == SignatureState.REQUIRED && isOptional(parameter.summary()))
                state = SignatureState.OPTIONAL;
            else if (isVariadic(parameter.summary()) && (state == SignatureState.REQUIRED || state == SignatureState.OPTIONAL))
                state = SignatureState.VARIADIC;

            if (state == SignatureState.OPTIONAL && isRequired(parameter.summary())) {
                holder.createErrorAnnotation(
                    parameter, String.format("Cannot put required parameter %s after optional parameters", parameter.getVariableName()));
                break;
            } else if (state == SignatureState.VARIADIC && isRequired(parameter.summary())) {
                holder.createErrorAnnotation(
                    parameter, String.format("Cannot put required parameter %s after variadic parameters", parameter.getVariableName()));
                break;
            } else if (state == SignatureState.VARIADIC && isOptional(parameter.summary())) {
                holder.createErrorAnnotation(
                    parameter, String.format("Cannot put optional parameter %s after variadic parameters", parameter.getVariableName()));
            }
        }
    }

    private static boolean isRequired(String summary) {
        return summary.equals("$") || summary.equals("@") ||
               summary.equals("%") || summary.equals("&");
    }

    private static boolean isOptional(String summary) {
        return summary.endsWith("?");
    }

    private static boolean isVariadic(String summary) {
        return summary.startsWith("*") || summary.startsWith("|") ||
               summary.startsWith("+") || summary.startsWith(":");
    }

    enum SignatureState {
        REQUIRED, OPTIONAL, VARIADIC
    }
}
