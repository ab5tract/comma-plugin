package org.raku.comma.findUsages;

import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.raku.comma.parsing.RakuWordsScanner;
import org.raku.comma.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class RakuFindUsagesProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new RakuWordsScanner();
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return HelpID.FIND_OTHER_USAGES;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof RakuConstant)
            return "Raku constant";
        else if (element instanceof RakuEnum)
            return "Raku enum";
        else if (element instanceof RakuLabel)
            return "Raku label";
        else if (element instanceof RakuPackageDecl)
            return "Raku " + ((RakuPackageDecl)element).getPackageKind();
        else if (element instanceof RakuRoutineDecl)
            return "Raku " + ((RakuRoutineDecl)element).getRoutineKind();
        else if (element instanceof RakuParameterVariable)
            return "Raku parameter";
        else if (element instanceof RakuRegexDecl)
            return "Raku " + ((RakuRegexDecl)element).getRegexKind();
        else if (element instanceof RakuSubset)
            return "Raku subset";
        else if (element instanceof RakuVariableDecl) {
            String scope = ((RakuVariableDecl)element).getScope();
            return "Raku " + (scope.equals("has") ? "attribute" : "variable");
        }
        return "Raku element";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement)element).getName();
            return name == null ? "" : name;
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }
}
