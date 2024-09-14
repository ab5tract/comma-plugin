package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import com.intellij.util.containers.ContainerUtil;
import org.raku.comma.annotation.fix.RemoveUnusedRoutineFix;
import org.raku.comma.annotation.fix.MakeSubroutineExportedFix;
import org.raku.comma.highlighter.RakuHighlighter;
import org.raku.comma.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UnusedRoutineAnnotation implements Annotator {
    private static final Set<String> AUTOCALLED = ContainerUtil.newHashSet("MAIN", "USAGE", "EXPORT");

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuRoutineDecl routine))
            return;
        if (routine.getRoutineKind().equals("method") && routine.isPrivate()) {
            // Private routines should be used within the enclosing package, so long
            // as it's not a role.
            RakuPackageDecl thePackage = PsiTreeUtil.getParentOfType(routine, RakuPackageDecl.class);
            if (thePackage != null && !thePackage.getPackageKind().equals("role") && !thePackage.trustsOthers()) {
                LocalSearchScope searchScope = new LocalSearchScope(thePackage);
                annotateIfUnused(holder, routine, searchScope, "Unused private method");
            }
        }
        else if (routine.isSub()) {
            // Lexical subroutine declarations not exported or used as a value can be
            // marked as unused too. MAIN is called implicitly.
            String routineName = routine.getRoutineName();
            if (routineName == null || AUTOCALLED.contains(routineName))
                return;
            if (routine.isExported())
                return;
            PsiElement context = routine.getParent();
            if (context instanceof RakuScopedDecl) {
                if (!((RakuScopedDecl)context).getScope().equals("my"))
                    return;
                context = context.getParent();
            }
            if (!(context instanceof RakuStatement))
                return;

            // Find enclosing lexical and look for usages.
            RakuPsiScope usageScope = PsiTreeUtil.getParentOfType(routine, RakuPsiScope.class);
            if (usageScope == null)
                return;
            LocalSearchScope searchScope = new LocalSearchScope(usageScope);
            annotateIfUnused(holder, routine, searchScope, "Unused subroutine");
        }
    }

    private static void annotateIfUnused(@NotNull AnnotationHolder holder, RakuRoutineDecl routine,
                                         LocalSearchScope searchScope, String message) {
        Query<PsiReference> results = ReferencesSearch.search(routine, searchScope);
        if (results.findFirst() == null) {
            PsiElement identifier = routine.getNameIdentifier();
            if (identifier != null) {
                AnnotationBuilder annBuilder = holder.newAnnotation(HighlightSeverity.WEAK_WARNING, message)
                        .range(identifier)
                        .withFix(new RemoveUnusedRoutineFix())
                        .textAttributes(RakuHighlighter.UNUSED);
                if (routine.isSub())
                    annBuilder = annBuilder.withFix(new MakeSubroutineExportedFix());
                annBuilder.create();
            }
        }
    }
}
