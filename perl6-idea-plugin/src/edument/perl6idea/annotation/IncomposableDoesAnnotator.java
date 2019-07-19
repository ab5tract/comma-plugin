package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.annotation.fix.ChangeDoesToIsFix;
import edument.perl6idea.psi.Perl6Also;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6Trait;
import edument.perl6idea.psi.Perl6TypeName;
import org.jetbrains.annotations.NotNull;

public class IncomposableDoesAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6Trait) && !(element instanceof Perl6Also)) return;

        Perl6Trait trait = element instanceof Perl6Trait ? (Perl6Trait)element : ((Perl6Also)element).getTrait();
        if (trait == null) return;
        if (!trait.getTraitModifier().equals("does")) return;

        Perl6PackageDecl declaration = PsiTreeUtil.getParentOfType(trait, Perl6PackageDecl.class);
        if (declaration == null) return;

        Perl6TypeName typeName = PsiTreeUtil.findChildOfType(trait, Perl6TypeName.class);
        if (typeName == null) return;

        PsiReference ref = typeName.getReference();
        if (ref == null) return;

        PsiElement composedDeclaration = ref.resolve();
        if (!(composedDeclaration instanceof Perl6PackageDecl)) return;

        if (declaration.getPackageKind().equals("role") &&
            ((Perl6PackageDecl)composedDeclaration).getPackageKind().equals("class")) {
            holder.createErrorAnnotation(trait, "Role cannot compose a class")
                  .registerFix(new ChangeDoesToIsFix(trait));
        } else if (declaration.getPackageKind().equals("class") &&
                   ((Perl6PackageDecl)composedDeclaration).getPackageKind().equals("class")) {
            holder.createErrorAnnotation(trait, "Class cannot compose a class")
                  .registerFix(new ChangeDoesToIsFix(trait));
        }
    }
}
