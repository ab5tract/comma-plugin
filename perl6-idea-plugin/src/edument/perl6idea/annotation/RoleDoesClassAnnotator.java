package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.annotation.fix.ChangeDoesToIsFix;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6Trait;
import edument.perl6idea.psi.Perl6TypeName;
import org.jetbrains.annotations.NotNull;

public class RoleDoesClassAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6Trait)) return;

        Perl6Trait trait = (Perl6Trait)element;
        if (!trait.getTraitModifier().equals("does")) return;

        PsiElement declaration = trait.getParent();
        if (!(declaration instanceof Perl6PackageDecl)) return;

        Perl6TypeName typeName = PsiTreeUtil.findChildOfType(trait, Perl6TypeName.class);
        if (typeName == null) return;

        PsiReference ref = typeName.getReference();
        if (ref == null) return;

        PsiElement composedDeclaration = ref.resolve();
        if (!(composedDeclaration instanceof Perl6PackageDecl)) return;

        if (((Perl6PackageDecl)declaration).getPackageKind().equals("role") &&
            ((Perl6PackageDecl)composedDeclaration).getPackageKind().equals("class")) {
            holder.createErrorAnnotation(trait, "Role cannot compose a class")
                  .registerFix(new ChangeDoesToIsFix(trait.getTextOffset()));
        } else if (((Perl6PackageDecl)declaration).getPackageKind().equals("class") &&
                   ((Perl6PackageDecl)composedDeclaration).getPackageKind().equals("class")) {
            holder.createErrorAnnotation(trait, "Class cannot compose a class")
                  .registerFix(new ChangeDoesToIsFix(trait.getTextOffset()));
        }
    }
}
