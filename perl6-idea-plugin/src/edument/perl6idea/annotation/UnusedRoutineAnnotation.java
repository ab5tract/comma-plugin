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
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6RoutineDecl;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class UnusedRoutineAnnotation implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6RoutineDecl))
            return;
        Perl6RoutineDecl routine = (Perl6RoutineDecl)element;
        if (routine.getRoutineKind().equals("method") && routine.isPrivate()) {
            // Private routines should be used within the enclosing package, so long
            // as it's not a role.
            Perl6PackageDecl thePackage = PsiTreeUtil.getParentOfType(routine, Perl6PackageDecl.class);
            if (thePackage != null && !thePackage.getPackageKind().equals("role") && !thePackage.trustsOthers()) {
                LocalSearchScope searchScope = new LocalSearchScope(thePackage);
                Query<PsiReference> results = ReferencesSearch.search(routine, searchScope);
                if (results.findFirst() == null) {
                    PsiElement identifier = routine.getNameIdentifier();
                    if (identifier != null) {
                        holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Unused private method")
                            .range(identifier)
                            .textAttributes(Perl6Highlighter.UNUSED)
                            .create();
                    }
                }
            }
        }
    }
}
