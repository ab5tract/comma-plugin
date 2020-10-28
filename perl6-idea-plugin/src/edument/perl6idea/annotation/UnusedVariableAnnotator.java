package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import edument.perl6idea.highlighter.Perl6Highlighter;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

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

        // Go over the declared variables and look for usages of them.
        LocalSearchScope searchScope = new LocalSearchScope(usageScope);
        for (Perl6Variable variable : ((Perl6VariableDecl)element).getVariables()) {
            // We need two references, since the declaration resolves to itself.
            PsiElement expectedResolution = variable.getParent() instanceof Perl6ParameterVariable
                                            ? variable.getParent()
                                            : element;
            Query<PsiReference> results = ReferencesSearch.search(expectedResolution, searchScope);
            AtomicInteger count = new AtomicInteger();
            results.forEach(found -> count.incrementAndGet() < 2);

            // Annotate if not found.
            if (count.get() < 2) {
                holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Unused variable")
                  .range(variable)
                  .textAttributes(Perl6Highlighter.UNUSED)
                  .create();
            }
        }
    }
}
