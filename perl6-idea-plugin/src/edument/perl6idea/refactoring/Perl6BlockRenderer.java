package edument.perl6idea.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;

public class Perl6BlockRenderer {
    public static <T extends PsiElement> String renderBlock(T t) {
        Perl6PsiScope scope = (Perl6PsiScope)PsiTreeUtil.findFirstParent(t, e -> e instanceof Perl6PsiScope);
        if (scope instanceof Perl6File) {
            return "Top level";
        } else if (scope instanceof Perl6BlockOrHash) {
            return "Control statement block";
        } else if (scope instanceof Perl6RoutineDecl) {
            String name = ((Perl6RoutineDecl)scope).getRoutineName();
            return String.format("%s %s",
                                 ((Perl6RoutineDecl)scope).getRoutineKind(),
                                 name == null ? "<anon>" : name);
        } else if (scope instanceof Perl6PackageDecl) {
            String name = ((Perl6PackageDecl) scope).getPackageName();
            return String.format("%s %s",
                    ((Perl6PackageDecl) scope).getPackageKind(),
                    name == null ? "<anon>" : name);
        } else if (scope instanceof Perl6Block) {
            return scope.getParent().getText().trim();
        } else {
            return t.getText().trim();
        }
    }
}
