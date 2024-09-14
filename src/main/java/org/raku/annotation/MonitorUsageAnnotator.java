package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.raku.annotation.fix.AddUseModuleFix;
import org.raku.psi.*;
import org.raku.utils.RakuUseUtils;
import org.jetbrains.annotations.NotNull;

public class MonitorUsageAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuPackageDecl)) return;

        String declarator = ((RakuPackageDecl)element).getPackageKind();
        if (!declarator.equals("monitor")) return;
        if (RakuUseUtils.usesModule(element, "OO::Monitors")) return;

        int elementTextOffset = element.getTextOffset();
        String packageName = ((RakuPackageDecl)element).getPackageName();
        if (packageName == null) return;
        holder.newAnnotation(HighlightSeverity.ERROR, "Cannot use monitor type package without OO::Monitors module being included")
            .range(new TextRange(elementTextOffset, elementTextOffset + packageName.length()))
            .withFix(new AddUseModuleFix("OO::Monitors")).create();
    }
}
