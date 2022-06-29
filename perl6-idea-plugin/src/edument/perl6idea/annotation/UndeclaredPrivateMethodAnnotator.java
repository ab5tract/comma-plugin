package edument.perl6idea.annotation;

import com.intellij.lang.annotation.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.annotation.fix.StubMissingPrivateMethodFix;
import edument.perl6idea.psi.Perl6MethodCall;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6Self;
import org.jetbrains.annotations.NotNull;

public class UndeclaredPrivateMethodAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6MethodCall))
            return;
        final Perl6MethodCall call = (Perl6MethodCall)element;
        String methodName = call.getCallName();
        PsiElement caller = call.getPrevSibling();

        // Annotate only private methods for now
        if (!methodName.startsWith("!")) return;

        // We can't safely do this analysis in a role, because the method might be
        // used in a role consumer (and that may even be in another module).
        Perl6PackageDecl enclosingPackage = PsiTreeUtil.getParentOfType(element, Perl6PackageDecl.class);
        if (enclosingPackage != null && enclosingPackage.getPackageKind().equals("role"))
            return;

        PsiReference reference = call.getReference();
        if (!(reference instanceof PsiPolyVariantReference)) return;
        ResolveResult[] declaration = ((PsiPolyVariantReference)reference).multiResolve(false);
        if (declaration.length != 0)
            return;
        PsiElement prev = call.getPrevSibling();
        if (prev instanceof Perl6RoutineDecl) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Subroutine cannot start with '!'")
                .range(element).create();
        } else {
            int offset = call.getTextOffset();
            AnnotationBuilder annBuilder = holder.newAnnotation(
                HighlightSeverity.ERROR, String.format("Private method %s is used, but not declared", methodName))
                .range(new TextRange(offset, offset + methodName.length()));
            if (caller instanceof Perl6Self)
                annBuilder = annBuilder.withFix(new StubMissingPrivateMethodFix(methodName, call));
            annBuilder.create();
        }
    }
}
