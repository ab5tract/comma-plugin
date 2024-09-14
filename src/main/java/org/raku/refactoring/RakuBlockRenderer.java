package org.raku.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.*;

public class RakuBlockRenderer {
    public static <T extends PsiElement> String renderBlock(T t) {
        RakuPsiScope scope = (RakuPsiScope)PsiTreeUtil.findFirstParent(t, e -> e instanceof RakuPsiScope);
        if (scope instanceof RakuFile) {
            return "Top level";
        } else if (scope instanceof RakuBlockOrHash) {
            return "Control statement block";
        } else if (scope instanceof RakuRoutineDecl) {
            String name = ((RakuRoutineDecl)scope).getRoutineName();
            return String.format("%s %s",
                                 ((RakuRoutineDecl)scope).getRoutineKind(),
                                 name == null ? "<anon>" : name);
        } else if (scope instanceof RakuPackageDecl) {
            String name = ((RakuPackageDecl) scope).getPackageName();
            return String.format("%s %s",
                    ((RakuPackageDecl) scope).getPackageKind(),
                    name == null ? "<anon>" : name);
        } else if (scope instanceof RakuBlock) {
            return scope.getParent().getText().trim();
        } else {
            return t.getText().trim();
        }
    }
}
