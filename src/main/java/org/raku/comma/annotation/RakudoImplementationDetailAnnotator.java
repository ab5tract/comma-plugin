package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.raku.comma.psi.RakuMethodCall;
import org.raku.comma.psi.RakuSubCallName;
import org.raku.comma.psi.external.ExternalRakuRoutineDecl;
import org.jetbrains.annotations.NotNull;
import org.raku.comma.utils.CommaProjectUtil;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class RakudoImplementationDetailAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element,
                         @NotNull AnnotationHolder holder) {
        if (CommaProjectUtil.isProjectRakudo(element)) return;
        if (element instanceof RakuSubCallName) {
            PsiReference reference = element.getReference();
            if (reference == null) return;
            PsiElement resolution = reference.resolve();
            if (resolution instanceof ExternalRakuRoutineDecl && ((ExternalRakuRoutineDecl) resolution).isImplementationDetail()) {
                String message = String.format("The '&%s' routine is implementation detail", ((RakuSubCallName) element).getCallName());
                holder.newAnnotation(HighlightSeverity.WARNING, message).range(element).create();
            }
        } else if (element instanceof RakuMethodCall) {
            PsiReference reference = element.getReference();
            if (reference == null) return;
            PsiElement resolution = reference.resolve();
            if (resolution instanceof ExternalRakuRoutineDecl && ((ExternalRakuRoutineDecl) resolution).isImplementationDetail()) {
                String message = String.format("The '&%s' method is implementation detail", ((RakuMethodCall) element).getCallName());
                holder.newAnnotation(HighlightSeverity.WARNING, message).range(element).create();
            }
        }
    }
}
