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
        boolean positionalAllowed = true;

        for (PsiElement psiElement : params) {
            if (!(psiElement instanceof Perl6Parameter)) continue;
            Perl6Parameter parameter = (Perl6Parameter)psiElement;

            String summary = parameter.summary();

            // Chop off type to match e.g. `$` instead of `Str $`
            int spaceIndex = summary.indexOf(' ');
            if (spaceIndex != -1)
                summary = summary.substring(spaceIndex + 1);

            if (isFirst) {
                state = isPositional(summary) ? SignatureState.POSITIONAL :
                        isOptional(summary) ? SignatureState.OPTIONAL :
                        isNamed(summary) ? SignatureState.NAMED :
                        SignatureState.VARIADIC;
                isFirst = false;
                positionalAllowed = state == SignatureState.POSITIONAL;
            }

            // If we passed last POSITIONAL, break the flag
            if (state == SignatureState.POSITIONAL && !isPositional(summary))
                positionalAllowed = false;

            if (!positionalAllowed && isPositional(summary)) {
                String message = "";
                switch (state) {
                    case NAMED: {
                        message = "Cannot put positional parameter %s after a named parameter";
                        break;
                    }
                    case OPTIONAL: {
                        message = "Cannot put positional parameter %s after an optional parameter";
                        break;
                    }
                    case VARIADIC: {
                        message = "Cannot put positional parameter %s after a variadic parameter";
                        break;
                    }
                }
                holder.createErrorAnnotation(parameter, String.format(message, parameter.getVariableName()));
                return;
            }

            if (isOptional(summary) && state == SignatureState.VARIADIC) {
                holder.createErrorAnnotation(
                    parameter, String.format("Cannot put optional parameter %s after a variadic parameter",
                                             parameter.getVariableName()));
                return;
            }

            if (isOptional(summary) && state == SignatureState.NAMED) {
                holder.createErrorAnnotation(
                    parameter, String.format("Cannot put an optional parameter %s after a named parameter",
                                             parameter.getVariableName()));
                return;
            }

            if (isOptional(summary))
                state = SignatureState.OPTIONAL;
            else if (isNamed(summary))
                state = SignatureState.NAMED;
            else if (isVariadic(summary))
                state = SignatureState.VARIADIC;
        }
    }

    private static boolean isPositional(String summary) {
        return summary.equals("$") || summary.equals("@") ||
               summary.equals("%") || summary.equals("&") ||
               (!isNamed(summary) && !isOptional(summary) && !isVariadic(summary)); // It may be plain type name
    }

    private static boolean isNamed(String summary) {
        return summary.startsWith(":");
    }

    private static boolean isOptional(String summary) {
        return summary.endsWith("?");
    }

    private static boolean isVariadic(String summary) {
        return summary.startsWith("*") || summary.startsWith("|") ||
               summary.startsWith("+");
    }

    enum SignatureState {
        POSITIONAL, NAMED, OPTIONAL, VARIADIC
    }
}
