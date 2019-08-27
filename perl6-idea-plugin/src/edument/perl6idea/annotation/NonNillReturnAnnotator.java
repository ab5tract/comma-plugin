package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6SubCall;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NonNillReturnAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6SubCall))
            return;

        Perl6SubCall call = (Perl6SubCall)element;
        if (!Objects.equals(call.getCallName(), "return"))
            return;

        if (call.getCallArguments().length == 0)
            return;

        Perl6RoutineDecl routineDecl = PsiTreeUtil.getParentOfType(call, Perl6RoutineDecl.class);
        if (routineDecl == null)
            return;

        String retType = routineDecl.getReturnType();
        if (!Objects.equals("Nil", retType))
            return;

        holder.createErrorAnnotation(call, "A value is returned from subroutine returning Nil");
    }
}
