package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.raku.comma.psi.RakuASTWrapperPsiElement;
import org.raku.comma.psi.RakuMethodCall;
import org.raku.comma.psi.RakuSubCallName;
import org.raku.comma.psi.external.ExternalRakuRoutineDecl;
import org.jetbrains.annotations.NotNull;
import org.raku.comma.utils.CommaProjectUtil;

public class RakudoImplementationDetailAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element,
                         @NotNull AnnotationHolder holder) {
        switch (element) {
            case RakuASTWrapperPsiElement rakuASTWrapperPsiElement when rakuASTWrapperPsiElement.isWithinRakudoCoreProject() -> {}
            case RakuSubCallName rakuSubCallName -> {
                PsiReference reference = element.getReference();
                if (reference == null) return;
                PsiElement resolution = reference.resolve();
                if (resolution instanceof ExternalRakuRoutineDecl && ((ExternalRakuRoutineDecl) resolution).isImplementationDetail()) {
                    String message = String.format("The '&%s' routine is implementation detail", ((RakuSubCallName) element).getCallName());
                    holder.newAnnotation(HighlightSeverity.WARNING, message).range(element).create();
                }
            }
            case RakuMethodCall rakuMethodCall -> {
                PsiReference reference = element.getReference();
                if (reference == null) return;
                PsiElement resolution = reference.resolve();
                if (resolution instanceof ExternalRakuRoutineDecl && ((ExternalRakuRoutineDecl) resolution).isImplementationDetail()) {
                    String message = String.format("The '&%s' method is implementation detail", ((RakuMethodCall) element).getCallName());
                    holder.newAnnotation(HighlightSeverity.WARNING, message).range(element).create();
                }
            }
            default -> {}
        }
    }
}
