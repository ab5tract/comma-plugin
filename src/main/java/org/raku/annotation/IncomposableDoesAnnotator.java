package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.annotation.fix.ChangeDoesToIsFix;
import org.raku.psi.*;
import org.raku.psi.RakuTypeName;
import org.raku.psi.RakuTrait;
import org.jetbrains.annotations.NotNull;

public class IncomposableDoesAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuTrait) && !(element instanceof RakuAlso)) return;

        RakuTrait trait = element instanceof RakuTrait ? (RakuTrait)element : ((RakuAlso)element).getTrait();
        if (trait == null) return;
        if (!trait.getTraitModifier().equals("does")) return;

        RakuPackageDecl declaration = PsiTreeUtil.getParentOfType(trait, RakuPackageDecl.class);
        if (declaration == null) return;

        RakuTypeName typeName = PsiTreeUtil.findChildOfType(trait, RakuTypeName.class);
        if (typeName == null) return;

        PsiReference ref = typeName.getReference();
        if (ref == null) return;

        PsiElement composedDeclaration = ref.resolve();
        if (!(composedDeclaration instanceof RakuPackageDecl)) return;

        if (declaration.getPackageKind().equals("role") &&
            ((RakuPackageDecl)composedDeclaration).getPackageKind().equals("class")) {
            holder.newAnnotation(HighlightSeverity.ERROR,"Role cannot compose a class")
                .range(trait).withFix(new ChangeDoesToIsFix(trait)).create();
        } else if (declaration.getPackageKind().equals("class") &&
                   ((RakuPackageDecl)composedDeclaration).getPackageKind().equals("class")) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Class cannot compose a class")
                .range(trait).withFix(new ChangeDoesToIsFix(trait)).create();
        }
    }
}
