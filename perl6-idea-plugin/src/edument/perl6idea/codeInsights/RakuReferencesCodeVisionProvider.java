package edument.perl6idea.codeInsights;

import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering;
import com.intellij.codeInsight.hints.codeVision.ReferencesCodeVisionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6ScopedDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RakuReferencesCodeVisionProvider extends ReferencesCodeVisionProvider {
    @Override
    public boolean acceptsFile(@NotNull PsiFile file) {
        return Perl6Language.INSTANCE == file.getLanguage();
    }

    @Override
    public boolean acceptsElement(@NotNull PsiElement element) {
        return element instanceof Perl6RoutineDecl || element instanceof Perl6PackageDecl ||
               element instanceof Perl6ScopedDecl;
    }

    @Nullable
    @Override
    public String getHint(@NotNull PsiElement element, @NotNull PsiFile file) {
        PsiElement elementToRefSearch = element;
        if (element.getParent() instanceof Perl6ScopedDecl)
            return null;
        if (element instanceof Perl6ScopedDecl) {
            elementToRefSearch = PsiTreeUtil.getChildOfAnyType(element, Perl6PackageDecl.class, Perl6RoutineDecl.class);
            if (elementToRefSearch == null)
                return null;
        }

        Collection<PsiReference> finds = ReferencesSearch.search(elementToRefSearch).findAll();
        return switch (finds.size()) {
            case 0 -> "No usages";
            case 1 -> "1 usage";
            default -> finds.size() + " usages";
        };
    }

    @NotNull
    @Override
    public List<CodeVisionRelativeOrdering> getRelativeOrderings() {
        return new ArrayList<>();
    }

    @NotNull
    @Override
    public String getId() {
        return "raku.references";
    }
}
