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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UnusedRoutineAnnotation implements Annotator {
    private static final Set<String> AUTOCALLED = new HashSet<>(Arrays.asList("MAIN", "USAGE", "EXPORT"));

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
                annotateIfUnused(holder, routine, searchScope, "Unused private method");
            }
        }
        else if (routine.getRoutineKind().equals("sub")) {
            // Lexical subroutine declarations not exported or used as a value can be
            // marked as unused too. MAIN is called implicitly.
            String routineName = routine.getRoutineName();
            if (routineName == null || AUTOCALLED.contains(routineName))
                return;
            if (routine.isExported())
                return;
            PsiElement context = routine.getParent();
            if (context instanceof Perl6ScopedDecl) {
                if (!((Perl6ScopedDecl)context).getScope().equals("my"))
                    return;
                context = context.getParent();
            }
            if (!(context instanceof Perl6Statement))
                return;

            // Find enclosing lexical and look for usages.
            Perl6PsiScope usageScope = PsiTreeUtil.getParentOfType(routine, Perl6PsiScope.class);
            if (usageScope == null)
                return;
            LocalSearchScope searchScope = new LocalSearchScope(usageScope);
            annotateIfUnused(holder, routine, searchScope, "Unused subroutine");
        }
    }

    private static void annotateIfUnused(@NotNull AnnotationHolder holder, Perl6RoutineDecl routine,
                                         LocalSearchScope searchScope, String message) {
        Query<PsiReference> results = ReferencesSearch.search(routine, searchScope);
        if (results.findFirst() == null) {
            PsiElement identifier = routine.getNameIdentifier();
            if (identifier != null) {
                holder.newAnnotation(HighlightSeverity.WEAK_WARNING, message)
                    .range(identifier)
                    .textAttributes(Perl6Highlighter.UNUSED)
                    .create();
            }
        }
    }
}
