package org.raku.comma.codeInsights;

import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering;
import com.intellij.codeInsight.hints.codeVision.ReferencesCodeVisionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.RakuLanguage;
import org.raku.comma.psi.RakuPackageDecl;
import org.raku.comma.psi.RakuRoutineDecl;
import org.raku.comma.psi.RakuScopedDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RakuReferencesCodeVisionProvider extends ReferencesCodeVisionProvider {
    @Override
    public boolean acceptsFile(@NotNull PsiFile file) {
        return RakuLanguage.getInstance() == file.getLanguage();
    }

    @Override
    public boolean acceptsElement(@NotNull PsiElement element) {
        return element instanceof RakuRoutineDecl || element instanceof RakuPackageDecl ||
               element instanceof RakuScopedDecl;
    }

    @Nullable
    @Override
    public String getHint(@NotNull PsiElement element, @NotNull PsiFile file) {
        PsiElement elementToRefSearch = element;
        if (element.getParent() instanceof RakuScopedDecl)
            return null;
        if (element instanceof RakuScopedDecl) {
            elementToRefSearch = PsiTreeUtil.getChildOfAnyType(element, RakuPackageDecl.class, RakuRoutineDecl.class);
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
