package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6Signature;
import edument.perl6idea.psi.Perl6Variable;
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
            }

            // Positionals go first
            if (state != SignatureState.POSITIONAL && isPositional(summary)) {
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
                holder.newAnnotation(HighlightSeverity.ERROR, String.format(message, parameter.getVariableName()))
                    .range(parameter).create();
                return;
            }

            // Optionals go before variadic
            if (state == SignatureState.VARIADIC && isOptional(summary)) {
                holder.newAnnotation(HighlightSeverity.ERROR,
                                     String.format("Cannot put optional parameter %s after a variadic parameter",
                                                   parameter.getVariableName()))
                    .range(parameter).create();
                return;
            }

            // Optionals go before named
            if (isOptional(summary) && !isNamed(summary) && state == SignatureState.NAMED) {
                    holder.newAnnotation(HighlightSeverity.ERROR,
                                         String.format("Cannot put an optional parameter %s after a named parameter",
                                                       parameter.getVariableName()))
                        .range(parameter).create();
                return;
            }


            if (isOptional(summary) && isNamed(summary)) {
                holder.newAnnotation(HighlightSeverity.WARNING,
                                     String.format("Explicit `?` on a named parameter %s is redundant, as all nameds are optional by default",
                                                   parameter.getVariableName()))
                  .range(parameter).create();
            }
            else if (isPositional(summary) && isRequired(summary)) {
                holder.newAnnotation(HighlightSeverity.WARNING,
                                     String.format("Explicit `!` on a positional parameter %s is redundant, as all positional parameters are required by default",
                                                   parameter.getVariableName()))
                  .range(parameter).create();
            }
            // Cheat here a bit, because summary returns ? for a parameter with explicit ? or with an initializer, so we cannot know
            // what is what, same with required parameter
            else if (parameter.getText().contains("?") && parameter.getInitializer() != null) {
                holder.newAnnotation(HighlightSeverity.WARNING,
                                     String.format("Explicit `?` on a parameter %s with default is redundant, as all parameters with default value are optional by default",
                                                   parameter.getVariableName()))
                  .range(parameter).create();
            }
            else if (parameter.getText().contains("!") && parameter.getInitializer() != null
                    && Perl6Variable.getTwigil(parameter.getVariableName()) != '!') {
                holder.newAnnotation(HighlightSeverity.ERROR,
                                     String.format("Parameter %s has a default value and so cannot be required", parameter.getVariableName()))
                  .range(parameter).create();
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

    private static boolean isRequired(String summary) {
        return summary.endsWith("!");
    }

    private static boolean isVariadic(String summary) {
        return summary.startsWith("*") || summary.startsWith("|") ||
               summary.startsWith("+");
    }

    enum SignatureState {
        POSITIONAL, NAMED, OPTIONAL, VARIADIC
    }
}
