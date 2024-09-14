package org.raku.comma.cro.template.findUsages;

import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.raku.comma.cro.template.psi.CroTemplateMacro;
import org.raku.comma.cro.template.psi.CroTemplateParameter;
import org.raku.comma.cro.template.psi.CroTemplateSub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CroTemplateFindUsagesProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new CroTemplateWordsScannar();
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return element instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return HelpID.FIND_OTHER_USAGES;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof CroTemplateMacro)
            return "Cro template macro";
        if (element instanceof CroTemplateSub)
            return "Cro template sub";
        if (element instanceof CroTemplateParameter)
            return "Cro template parameter";
        return "Cro template element";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        String name = ((PsiNamedElement)element).getName();
        return name == null ? "" : name;
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }
}
