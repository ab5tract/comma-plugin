package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.symbols.Perl6SymbolContributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Perl6PsiDeclaration extends Perl6PsiElement, PsiNameIdentifierOwner, Perl6SymbolContributor {
    String getScope();

    default List<Perl6Trait> getTraits() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, Perl6Trait.class);
    }

    @Nullable
    default Perl6Trait findTrait(String mod, String name) {
        for (Perl6Trait trait : getTraits())
            if (mod.equals(trait.getTraitModifier()) &&
                name.equals(trait.getTraitName()))
                return trait;
        return null;
    }

    default boolean isExported() {
        return findTrait("is", "export") != null;
    }

    default String getGlobalName() {
        // If it's not an our-scoped thing, no global name.
        String scope = getScope();
        if (!(scope.equals("our") || scope.equals("unit")))
            return null;

        // Walk up any enclosing packages.
        String globalName = getName();
        PsiElement outer = getParent();
        while (outer != null && !(outer instanceof Perl6File)) {
            if (outer instanceof Perl6PackageDecl) {
                Perl6PackageDecl pkg = (Perl6PackageDecl)outer;
                if (pkg.getScope().equals("my"))
                    return null;
                globalName = pkg.getName() + "::" + globalName;
            }
            outer = outer.getParent();
        }
        return globalName;
    }
}