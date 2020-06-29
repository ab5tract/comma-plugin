package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.annotation.fix.AddMonitorModuleFix;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MonitorUsageAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6PackageDecl)) return;

        String declarator = ((Perl6PackageDecl)element).getPackageKind();
        if (!declarator.equals("monitor")) return;

        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(element, Perl6PsiScope.class);
        int elementTextOffset = element.getTextOffset();
        while (scope != null) {
            Perl6StatementList list = PsiTreeUtil.findChildOfType(scope, Perl6StatementList.class);
            if (list == null) break;
            Perl6Statement[] stats = PsiTreeUtil.getChildrenOfType(list, Perl6Statement.class);
            if (stats == null) stats = new Perl6Statement[0];
            for (Perl6Statement statement : stats) {
                if (statement.getTextOffset() > elementTextOffset) break;
                for (PsiElement child : statement.getChildren()) {
                    if (!(child instanceof Perl6UseStatement)) continue;
                    String moduleName = ((Perl6UseStatement)child).getModuleName();
                    if (Objects.equals(moduleName, "OO::Monitors"))
                        return;
                }
            }
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }


        String packageName = ((Perl6PackageDecl)element).getPackageName();
        if (packageName == null) return;
        holder.newAnnotation(HighlightSeverity.ERROR, "Cannot use monitor type package without OO::Monitors module being included")
            .range(new TextRange(elementTextOffset, elementTextOffset + packageName.length()))
            .withFix(new AddMonitorModuleFix()).create();
    }
}
