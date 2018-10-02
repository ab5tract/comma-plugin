package edument.perl6idea.findUsages;

import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import edument.perl6idea.parsing.Perl6WordsScanner;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            return "Perl 6 constant";
        else if (element instanceof Perl6Enum)
            return "Perl 6 enum";
        else if (element instanceof Perl6Label)
            return "Perl 6 label";
        else if (element instanceof Perl6PackageDecl)
            return "Perl 6 " + ((Perl6PackageDecl)element).getPackageKind();
        else if (element instanceof Perl6RoutineDecl)
            return "Perl 6 " + ((Perl6RoutineDecl)element).getRoutineKind();
        else if (element instanceof Perl6ParameterVariable)
            return "Perl 6 parameter";
        else if (element instanceof Perl6RegexDecl)
            return "Perl 6 " + ((Perl6RegexDecl)element).getRegexKind();
        else if (element instanceof Perl6Subset)
            return "Perl 6 subset";
        else if (element instanceof Perl6VariableDecl) {
            String scope = ((Perl6VariableDecl)element).getScope();
            return "Perl 6 " + (scope.equals("has") ? "attribute" : "variable");
        }
        return "Perl 6 element";
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
