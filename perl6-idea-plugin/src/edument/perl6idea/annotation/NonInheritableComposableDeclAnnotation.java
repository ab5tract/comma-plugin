package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6Trait;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NonInheritableComposableDeclAnnotation implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6PackageDecl)) return;
        Perl6PackageDecl decl = (Perl6PackageDecl)element;
        String kind = decl.getPackageKind();
        if (!kind.equals("package") && !kind.equals("module")) return;

        List<Perl6Trait> traits = decl.getTraits();
        boolean compose = false;
        boolean inherit = false;
        for (int i = 0; i< traits.size(); i++) {
            String modifier = traits.get(i).getTraitModifier();
            if (modifier.equals("does")) {
                compose = true;
            }
            else if (modifier.equals("is")) {
                inherit = true;
            }
        }
        if (inherit || compose) {
            String message = decl.getPackageKind() + " cannot ";
            if (compose && inherit)
                message += "compose a role or inherit a class";
            else if (compose)
                message += "compose a role";
            else
                message += "inherit a class";
            holder.createErrorAnnotation(
                new TextRange(decl.getTextOffset(), decl.getTextOffset() + decl.getPackageName().length()),
                message);
        }
    }
}
