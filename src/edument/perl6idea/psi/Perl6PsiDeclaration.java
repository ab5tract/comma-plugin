package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface Perl6PsiDeclaration extends Perl6PsiElement, PsiNameIdentifierOwner {
    String getScope();

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
