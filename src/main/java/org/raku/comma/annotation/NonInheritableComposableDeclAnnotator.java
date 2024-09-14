package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.raku.comma.psi.RakuPackageDecl;
import org.raku.comma.psi.RakuTrait;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NonInheritableComposableDeclAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuPackageDecl decl)) return;
        String kind = decl.getPackageKind();
        if (!kind.equals("package") && !kind.equals("module")) return;

        List<RakuTrait> traits = decl.getTraits();
        String messageBase = decl.getPackageKind() + " cannot ";
        for (RakuTrait trait : traits) {
            String modifier = trait.getTraitModifier();
            if (modifier.equals("does")) {
                holder.newAnnotation(HighlightSeverity.ERROR, messageBase + "compose a role")
                    .range(trait).create();
            }
            else if (modifier.equals("is")) {
                holder.newAnnotation(HighlightSeverity.ERROR, messageBase + "inherit a class")
                    .range(trait).create();
            }
        }
    }
}
