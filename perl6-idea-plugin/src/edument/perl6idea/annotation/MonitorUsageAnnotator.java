package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import edument.perl6idea.annotation.fix.AddUseModuleFix;
import edument.perl6idea.psi.*;
import edument.perl6idea.utils.Perl6UseUtils;
import org.jetbrains.annotations.NotNull;

public class MonitorUsageAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6PackageDecl)) return;

        String declarator = ((Perl6PackageDecl)element).getPackageKind();
        if (!declarator.equals("monitor")) return;
        if (Perl6UseUtils.usesModule(element, "OO::Monitors")) return;

        int elementTextOffset = element.getTextOffset();
        String packageName = ((Perl6PackageDecl)element).getPackageName();
        if (packageName == null) return;
        holder.newAnnotation(HighlightSeverity.ERROR, "Cannot use monitor type package without OO::Monitors module being included")
            .range(new TextRange(elementTextOffset, elementTextOffset + packageName.length()))
            .withFix(new AddUseModuleFix("OO::Monitors")).create();
    }
}
