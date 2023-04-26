package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6MethodCall;
import edument.perl6idea.psi.Perl6SubCallName;
import edument.perl6idea.psi.external.ExternalPerl6RoutineDecl;
import org.jetbrains.annotations.NotNull;

public class RakudoImplementationDetailAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element,
                         @NotNull AnnotationHolder holder) {
        if (element instanceof Perl6SubCallName) {
            PsiReference reference = element.getReference();
            if (reference == null)
                return;
            PsiElement resolution = reference.resolve();
            if (resolution instanceof ExternalPerl6RoutineDecl && ((ExternalPerl6RoutineDecl)resolution).isImplementationDetail()) {
                String message = String.format("The '&%s' routine is implementation detail", ((Perl6SubCallName)element).getCallName());
                holder.newAnnotation(HighlightSeverity.WARNING, message).range(element).create();
            }
        } else if (element instanceof Perl6MethodCall) {
            PsiReference reference = element.getReference();
            if (reference == null)
                return;
            PsiElement resolution = reference.resolve();
            if (resolution instanceof ExternalPerl6RoutineDecl && ((ExternalPerl6RoutineDecl)resolution).isImplementationDetail()) {
                String message = String.format("The '&%s' method is implementation detail", ((Perl6MethodCall)element).getCallName());
                holder.newAnnotation(HighlightSeverity.WARNING, message).range(element).create();
            }
        }
    }
}
