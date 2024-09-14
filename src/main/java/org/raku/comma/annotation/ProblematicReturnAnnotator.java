package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.*;
import org.jetbrains.annotations.NotNull;

public class ProblematicReturnAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof RakuSubCall) {
            RakuSubCallName name = PsiTreeUtil.getChildOfType(element, RakuSubCallName.class);
            if (name != null && name.getText().equals("return")) {
                checkReturn(holder, element);
            }
        }
    }

    private static void checkReturn(AnnotationHolder holder, PsiElement returnCall) {
        /* Look for the return target, or something blocking a return. */
        PsiElement curBlockoid = PsiTreeUtil.getParentOfType(returnCall, RakuBlockoid.class);
        while (curBlockoid != null) {
            PsiElement parent = curBlockoid.getParent();

            /* If we're in a routine, we're fine. */
            if (parent instanceof RakuRoutineDecl)
                return;

            /* Return in concurrency constructs is problematic. */
            if (parent instanceof RakuBlock)
                parent = parent.getParent();
            if (parent instanceof RakuStart) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot use return to produce a result in a start block")
                    .range(returnCall).create();
                return;
            }
            if (parent instanceof RakuReact) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot use return to exit a react block")
                    .range(returnCall).create();
                return;
            }
            if (parent instanceof RakuSupply) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot use return to exit a supply block")
                    .range(returnCall).create();
                return;
            }
            if (parent instanceof RakuWheneverStatement) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot use return in a whenever block")
                    .range(returnCall).create();
                return;
            }

            /* Look another block out. */
            curBlockoid = PsiTreeUtil.getParentOfType(curBlockoid, RakuBlockoid.class);
        }

        /* If we didn't find a return target, then we're outside of any routine. */
        holder.newAnnotation(HighlightSeverity.ERROR, "Return outside of routine")
            .range(returnCall).create();
    }
}
