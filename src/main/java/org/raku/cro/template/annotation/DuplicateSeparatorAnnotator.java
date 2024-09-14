package org.raku.cro.template.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.cro.template.psi.CroTemplateIteration;
import org.raku.cro.template.psi.CroTemplateSeparator;
import org.raku.cro.template.psi.CroTemplateTagSequence;
import org.jetbrains.annotations.NotNull;

public class DuplicateSeparatorAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof CroTemplateIteration))
            return;
        CroTemplateTagSequence body = PsiTreeUtil.getChildOfType(element, CroTemplateTagSequence.class);
        CroTemplateSeparator[] separators = PsiTreeUtil.getChildrenOfType(body, CroTemplateSeparator.class);
        if (separators == null)
            return;
        for (int i = 1; i < separators.length; i++)
            holder.newAnnotation(HighlightSeverity.ERROR, "Duplicate separator")
                    .range(separators[i])
                    .create();
    }
}
