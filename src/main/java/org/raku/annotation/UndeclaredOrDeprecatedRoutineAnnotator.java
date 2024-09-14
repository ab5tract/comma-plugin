package org.raku.annotation;

import com.intellij.lang.annotation.*;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.annotation.fix.ConstKeywordFix;
import org.raku.annotation.fix.StubMissingSubroutineFix;
import org.raku.highlighter.RakuHighlighter;
import org.raku.psi.*;
import org.raku.sdk.RakuSdkType;
import org.jetbrains.annotations.NotNull;

public class UndeclaredOrDeprecatedRoutineAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof final RakuSubCallName call))
            return;

        // Only do the analysis if the core setting symbols are available.
        RakuFile setting = RakuSdkType.getInstance().getCoreSettingFile(element.getProject());
        if (setting.getVirtualFile().getName().equals("DUMMY"))
            return;

        // Resolve the reference.
        String subName = call.getCallName();
        if (subName.equals("::"))
            return;
        PsiReferenceBase.Poly<?> reference = (PsiReferenceBase.Poly<?>)call.getReference();
        if (reference == null)
            return;
        ResolveResult[] results = reference.multiResolve(false);

        // If no resolve results, then we've got an error.
        if (results.length == 0) {
            AnnotationBuilder annBuilder = holder.newAnnotation(HighlightSeverity.ERROR,
                                                                String.format("Subroutine %s is not declared", subName))
                    .withFix(new StubMissingSubroutineFix())
                    .range(element);
            if (subName.equals("const") &&
                (PsiTreeUtil.skipWhitespacesForward(call) instanceof RakuVariable ||
                 PsiTreeUtil.skipWhitespacesForward(call) instanceof RakuInfixApplication)) {
                annBuilder = annBuilder.withFix(new ConstKeywordFix(call));
            }
            annBuilder.create();
        }

        else if (results.length == 1) {
            // If it resolves to a type, highlight it as one.
            PsiElement resolvedElement = results[0].getElement();
            if (resolvedElement instanceof RakuPackageDecl) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .textAttributes(RakuHighlighter.TYPE_NAME)
                    .create();
            }

            // If it resolves to a deprecated routine, highlight it as one.
            else if (resolvedElement instanceof RakuDeprecatable && ((RakuDeprecatable)resolvedElement).isDeprecated()) {
                String deprecationMessage = ((RakuDeprecatable)resolvedElement).getDeprecationMessage();
                String message = subName + " is deprecated" + (deprecationMessage != null ? "; use " + deprecationMessage : "");
                holder.newAnnotation(HighlightSeverity.WARNING, message)
                    .textAttributes(CodeInsightColors.DEPRECATED_ATTRIBUTES)
                    .create();
            }
        }
    }
}
