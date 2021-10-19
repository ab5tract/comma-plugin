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

            if (isFirst) {
                state = parameter.isPositional() ? SignatureState.POSITIONAL :
                        parameter.isNamed() ? SignatureState.NAMED :
                        parameter.isOptional() ? SignatureState.OPTIONAL :
                        SignatureState.VARIADIC;
                isFirst = false;
            }

            // Positionals go first
            if (state != SignatureState.POSITIONAL && parameter.isPositional() && !parameter.isOptional() &&
                !parameter.isExplicitlyOptional() && !parameter.isSlurpy()) {
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

            // Optionals go before variadic, but not named ones
            if (state == SignatureState.VARIADIC && parameter.isOptional() && !parameter.isNamed()) {
                holder.newAnnotation(HighlightSeverity.ERROR,
                                     String.format("Cannot put optional parameter %s after a variadic parameter",
                                                   parameter.getVariableName()))
                    .range(parameter).create();
                return;
            }

            // Optionals go before named
            if (parameter.isOptional() && !parameter.isNamed() && state == SignatureState.NAMED) {
                    holder.newAnnotation(HighlightSeverity.ERROR,
                                         String.format("Cannot put an optional parameter %s after a named parameter",
                                                       parameter.getVariableName()))
                        .range(parameter).create();
                return;
            }


            if (parameter.isExplicitlyOptional() && parameter.isNamed()) {
                holder.newAnnotation(HighlightSeverity.WARNING,
                                     String.format("Explicit `?` on a named parameter %s is redundant, as all nameds are optional by default",
                                                   parameter.getVariableName()))
                  .range(parameter).create();
            }
            else if (parameter.isRequired() && parameter.getInitializer() != null
                     && Perl6Variable.getTwigil(parameter.getVariableName()) != '!') {
                holder.newAnnotation(HighlightSeverity.ERROR,
                                     String.format("Parameter %s has a default value and so cannot be required", parameter.getVariableName()))
                  .range(parameter).create();
            }
            else if (parameter.isPositional() && parameter.isRequired()) {
                holder.newAnnotation(HighlightSeverity.WARNING,
                                     String.format("Explicit `!` on a positional parameter %s is redundant, as all positional parameters are required by default",
                                                   parameter.getVariableName()))
                  .range(parameter).create();
            }
            else if (parameter.isExplicitlyOptional() && parameter.getInitializer() != null) {
                holder.newAnnotation(HighlightSeverity.WARNING,
                                     String.format("Explicit `?` on a parameter %s with default is redundant, as all parameters with default value are optional by default",
                                                   parameter.getVariableName()))
                  .range(parameter).create();
            }

            if (parameter.isNamed())
                state = SignatureState.NAMED;
            else if (parameter.isOptional())
                state = SignatureState.OPTIONAL;
            else if (parameter.isSlurpy())
                state = SignatureState.VARIADIC;
        }
    }

    enum SignatureState {
        POSITIONAL, NAMED, OPTIONAL, VARIADIC
    }
}
