package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

public class ProblematicReturn implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof Perl6SubCall) {
            Perl6SubCallName name = PsiTreeUtil.getChildOfType(element, Perl6SubCallName.class);
            if (name != null && name.getText().equals("return")) {
                checkReturn(holder, element);
            }
        }
    }

    private void checkReturn(AnnotationHolder holder, PsiElement returnCall) {
        /* Look for the return target, or something blocking a return. */
        PsiElement curBlockoid = PsiTreeUtil.getParentOfType(returnCall, Perl6Blockoid.class);
        while (curBlockoid != null) {
            PsiElement parent = curBlockoid.getParent();

            /* If we're in a routine, we're fine. */
            if (parent instanceof Perl6RoutineDecl)
                return;

            /* Return in concurrency constructs is problematic. */
            if (parent instanceof Perl6Block)
                parent = parent.getParent();
            if (parent instanceof Perl6Start) {
                holder.createErrorAnnotation(returnCall, "Cannot use return to produce a result in a start block");
                return;
            }
            if (parent instanceof Perl6React) {
                holder.createErrorAnnotation(returnCall, "Cannot use return to exit a react block");
                return;
            }
            if (parent instanceof Perl6Supply) {
                holder.createErrorAnnotation(returnCall, "Cannot use return to exit a supply block");
                return;
            }
            if (parent instanceof Perl6WheneverStatement) {
                holder.createErrorAnnotation(returnCall, "Cannot use return in a whenever block");
                return;
            }

            /* Look another block out. */
            curBlockoid = PsiTreeUtil.getParentOfType(curBlockoid, Perl6Blockoid.class);
        }

        /* If we didn't find a return target, then we're outside of any routine. */
        holder.createErrorAnnotation(returnCall, "Return outside of routine");
    }
}
