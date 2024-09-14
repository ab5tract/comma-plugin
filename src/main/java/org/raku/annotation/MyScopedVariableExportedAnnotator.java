package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.raku.annotation.fix.ChangeMyScopeToOurScopeFix;
import org.raku.psi.RakuScopedDecl;
import org.raku.psi.RakuVariableDecl;
import org.jetbrains.annotations.NotNull;

public class MyScopedVariableExportedAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuScopedDecl decl)) return;

        if (!decl.getScope().equals("my")) return;
        if (!(decl.getLastChild() instanceof RakuVariableDecl variableDecl)) return;

        if (!variableDecl.isExported()) return;

        // If variable is exported and the scope is `my`, annotate
        holder.newAnnotation(HighlightSeverity.ERROR, "`my` scoped variable cannot be exported")
            .range(element).withFix(new ChangeMyScopeToOurScopeFix(decl.getTextOffset())).create();
    }
}
