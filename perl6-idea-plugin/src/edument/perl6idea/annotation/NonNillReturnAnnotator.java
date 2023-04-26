package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.sdk.Perl6SettingTypeId;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NonNillReturnAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6SubCall call))
            return;

        if (!Objects.equals(call.getCallName(), "return"))
            return;

        if (call.getCallArguments().length == 0)
            return;

        Perl6RoutineDecl routineDecl = PsiTreeUtil.getParentOfType(call, Perl6RoutineDecl.class);
        if (routineDecl == null)
            return;

        Perl6Type retType = routineDecl.getReturnType();
        Perl6Type nilType = Perl6SdkType.getInstance().getCoreSettingType(routineDecl.getProject(), Perl6SettingTypeId.Nil);
        if (!Objects.equals(nilType, retType))
            return;

        holder.newAnnotation(HighlightSeverity.ERROR, "A value is returned from subroutine returning Nil")
            .range(call).create();
    }
}
