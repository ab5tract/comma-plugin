package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.annotation.fix.UseDirectAttributeAccessFix;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

public class SelfAvailabilityAnnotation implements Annotator {
    private enum Availability { NONE, PARTIAL, FULL }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // The self PSI element shows up when we literally have the `self` token, or
        // when we have `$.foo` (it's the `$` in this case). Since annotators can only
        // be used to annotate things below what they're invoked on, we'll have to
        // handle these cases separately.
        if (element instanceof Perl6Self && element.getTextLength() == 4) {
            // Literal self. If there's no self available at all, complain about that.
            if (getAvailability(element) == Availability.NONE) {
                holder.newAnnotation(HighlightSeverity.ERROR, "No invocant is available here")
                    .create();
            }
        }
        else if (element instanceof Perl6PostfixApplication) {
            PsiElement caller = ((Perl6PostfixApplication)element).getOperand();
            if (caller instanceof Perl6Self && caller.getTextLength() == 1) {
                Availability availability = getAvailability(element);
                if (availability == Availability.NONE) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "No invocant is available here")
                        .create();
                }
                else if (availability == Availability.PARTIAL) {
                    AnnotationBuilder builder = holder.newAnnotation(HighlightSeverity.ERROR,
                     "Virtual method calls are not allowed on partially constructed objects");

                    // It's probably of the form $.foo, so offer an intention to turn it
                    // into $!foo.
                    PsiElement postfix = ((Perl6PostfixApplication)element).getPostfix();
                    if (postfix instanceof Perl6MethodCall && ((Perl6MethodCall)postfix).getCallArguments().length == 0) {
                        PsiElement name = ((Perl6MethodCall)postfix).getSimpleName();
                        if (name != null) {
                            String newAttributeName = caller.getText() + "!" + name.getText();
                            builder = builder.newFix(new UseDirectAttributeAccessFix((Perl6PostfixApplication)element, newAttributeName))
                                    .registerFix();
                        }
                    }

                    builder.create();
                }
            }
        }
    }

    private static Availability getAvailability(PsiElement from) {
        while (true) {
            // self can be provided by being in a method or the initializer of a variable
            // declaration of scope `has`.
            Perl6PsiDeclaration possibleSelfProvider = PsiTreeUtil.getParentOfType(from, Perl6RoutineDecl.class,
                    Perl6RegexDecl.class, Perl6VariableDecl.class);
            if (possibleSelfProvider instanceof Perl6RoutineDecl) {
                String kind = ((Perl6RoutineDecl)possibleSelfProvider).getRoutineKind();
                if ("method".equals(kind))
                    return Availability.FULL;
                if ("submethod".equals(kind))
                    return Availability.PARTIAL;
                // Could be a sub within a context that provides a self, so keep going.
                from = possibleSelfProvider;
            }
            else if (possibleSelfProvider instanceof Perl6RegexDecl) {
                return Availability.FULL;
            }
            else if (possibleSelfProvider instanceof Perl6VariableDecl) {
                String scope = possibleSelfProvider.getScope();
                if (scope.equals("has") || scope.equals("HAS"))
                    return Availability.PARTIAL;
                // Could just be a variable decl in a context that provides a self, so
                // keep searching.
                from = possibleSelfProvider;
            }
            else {
                return Availability.NONE;
            }
        }
    }
}
