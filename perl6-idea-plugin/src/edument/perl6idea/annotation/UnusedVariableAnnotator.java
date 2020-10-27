package edument.perl6idea.annotation;

import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.highlighter.Perl6Highlighter;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

public class UnusedVariableAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // Make sure it's a lexical variable declaration.
        if (!(element instanceof Perl6VariableDecl))
            return;
        PsiElement maybeScopedDecl = element.getParent();
        if (!(maybeScopedDecl instanceof Perl6ScopedDecl))
            return;
        String scope = ((Perl6ScopedDecl)maybeScopedDecl).getScope();
        if (!(scope.equals("my") || scope.equals("state")))
            return;

        // Obtain the scope that they should exist within.
        Perl6PsiScope usageScope = PsiTreeUtil.getParentOfType(element, Perl6PsiScope.class);
        if (usageScope == null)
            return;
        String usageScopeSource = usageScope.getText();

        // Go over the declared variables.
        for (Perl6Variable variable : ((Perl6VariableDecl)element).getVariables()) {
            // For anything without & sigil, we search for the variable name itself,
            // and then make sure we find at least one reference that resolves to the
            // variable declaration (that is not the declaration itself). For the &
            // sigil, we could have `my &foo = ...; foo()` and so we need to look for
            // the function name.
            String name = variable.getVariableName();
            if (name == null || name.length() < 2)
                continue;
            if (Perl6Variable.getTwigil(name) != ' ')
                continue;
            char sigil = Perl6Variable.getSigil(name);
            String search = sigil == '&' ? name.substring(1) : name;
            PsiElement expectedResolution = variable.getParent() instanceof Perl6ParameterVariable
                ? variable.getParent()
                : element;
            int curIndex = usageScopeSource.indexOf(search);
            int found = 0;
            while (curIndex > 0) {
                PsiReference reference = usageScope.findReferenceAt(curIndex);
                if (reference != null) {
                    PsiElement resolution = reference.resolve();
                    if (resolution != null && PsiEquivalenceUtil.areElementsEquivalent(resolution, expectedResolution)) {
                        found++;
                        if (found == 2) // First finding is the declaration
                            break;
                    }
                }
                curIndex = usageScopeSource.indexOf(search, curIndex + 1);
            }

            // Annotate if not found.
            if (found != 2) {
                holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Unused variable")
                    .range(variable)
                    .textAttributes(Perl6Highlighter.UNUSED)
                    .create();
            }
        }
    }
}
