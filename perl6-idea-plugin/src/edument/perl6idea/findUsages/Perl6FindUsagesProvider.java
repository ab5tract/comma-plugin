package edument.perl6idea.findUsages;

import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import edument.perl6idea.parsing.Perl6WordsScanner;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class Perl6FindUsagesProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new Perl6WordsScanner();
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
        if (element instanceof Perl6Constant)
            return "Raku constant";
        else if (element instanceof Perl6Enum)
            return "Raku enum";
        else if (element instanceof Perl6Label)
            return "Raku label";
        else if (element instanceof Perl6PackageDecl)
            return "Raku " + ((Perl6PackageDecl)element).getPackageKind();
        else if (element instanceof Perl6RoutineDecl)
            return "Raku " + ((Perl6RoutineDecl)element).getRoutineKind();
        else if (element instanceof Perl6ParameterVariable)
            return "Raku parameter";
        else if (element instanceof Perl6RegexDecl)
            return "Raku " + ((Perl6RegexDecl)element).getRegexKind();
        else if (element instanceof Perl6Subset)
            return "Raku subset";
        else if (element instanceof Perl6VariableDecl) {
            String scope = ((Perl6VariableDecl)element).getScope();
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
