package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.raku.comma.psi.RakuMethodCall;
import org.raku.comma.psi.RakuSubCallName;
import org.raku.comma.psi.external.ExternalRakuRoutineDecl;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RakudoImplementationDetailAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element,
                         @NotNull AnnotationHolder holder)
    {
        if (isProjectRakudo()) return;
        if (element instanceof RakuSubCallName) {
            PsiReference reference = element.getReference();
            if (reference == null) return;
            PsiElement resolution = reference.resolve();
            if (resolution instanceof ExternalRakuRoutineDecl && ((ExternalRakuRoutineDecl)resolution).isImplementationDetail()) {
                String message = String.format("The '&%s' routine is implementation detail", ((RakuSubCallName)element).getCallName());
                holder.newAnnotation(HighlightSeverity.WARNING, message).range(element).create();
            }
        } else if (element instanceof RakuMethodCall) {
            PsiReference reference = element.getReference();
            if (reference == null) return;
            PsiElement resolution = reference.resolve();
            if (resolution instanceof ExternalRakuRoutineDecl && ((ExternalRakuRoutineDecl)resolution).isImplementationDetail()) {
                String message = String.format("The '&%s' method is implementation detail", ((RakuMethodCall)element).getCallName());
                holder.newAnnotation(HighlightSeverity.WARNING, message).range(element).create();
            }
        }
    }

    // TODO: Replace this with a project-level setting or something else more robust
    private Boolean PROJECT_IS_RAKUDO = null;
    private boolean isProjectRakudo() {
        if (PROJECT_IS_RAKUDO == null) {
            PROJECT_IS_RAKUDO = Objects.requireNonNull(ProjectManager.getInstance().getOpenProjects()[0].getBasePath()).endsWith("rakudo");
        }
        return PROJECT_IS_RAKUDO;
    }
}
