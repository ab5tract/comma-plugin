package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.annotation.fix.UseDirectAttributeAccessFix;
import org.raku.psi.*;
import org.jetbrains.annotations.NotNull;

public class SelfAvailabilityAnnotation implements Annotator {
    private enum Availability { NONE, PARTIAL, FULL }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // The self PSI element shows up when we literally have the `self` token, or
        // when we have `$.foo` (it's the `$` in this case). Since annotators can only
        // be used to annotate things below what they're invoked on, we'll have to
        // handle these cases separately.
        if (element instanceof RakuSelf && element.getTextLength() == 4) {
            // Literal self. If there's no self available at all, complain about that.
            if (getAvailability(element) == Availability.NONE) {
                holder.newAnnotation(HighlightSeverity.ERROR, "No invocant is available here")
                    .create();
            }
        }
        else if (element instanceof RakuPostfixApplication) {
            PsiElement caller = ((RakuPostfixApplication)element).getOperand();
            if (caller instanceof RakuSelf && caller.getTextLength() == 1) {
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
                    PsiElement postfix = ((RakuPostfixApplication)element).getPostfix();
                    if (postfix instanceof RakuMethodCall && ((RakuMethodCall)postfix).getCallArguments().length == 0) {
                        PsiElement name = ((RakuMethodCall)postfix).getSimpleName();
                        if (name != null) {
                            String newAttributeName = caller.getText() + "!" + name.getText();
                            builder = builder.newFix(new UseDirectAttributeAccessFix((RakuPostfixApplication)element, newAttributeName))
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
            RakuPsiDeclaration possibleSelfProvider = PsiTreeUtil.getParentOfType(from, RakuRoutineDecl.class,
                                                                                  RakuRegexDecl.class, RakuVariableDecl.class);
            if (possibleSelfProvider instanceof RakuRoutineDecl) {
                String kind = ((RakuRoutineDecl)possibleSelfProvider).getRoutineKind();
                if ("method".equals(kind))
                    return Availability.FULL;
                if ("submethod".equals(kind))
                    return Availability.PARTIAL;
                // Could be a sub within a context that provides a self, so keep going.
                from = possibleSelfProvider;
            }
            else if (possibleSelfProvider instanceof RakuRegexDecl) {
                return Availability.FULL;
            }
            else if (possibleSelfProvider instanceof RakuVariableDecl) {
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
