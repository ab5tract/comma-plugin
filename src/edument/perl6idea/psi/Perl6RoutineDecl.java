package edument.perl6idea.psi;

import com.intellij.psi.PsiNameIdentifierOwner;

public interface Perl6RoutineDecl extends Perl6PsiElement, Perl6PsiScope, Perl6PsiDeclaration, PsiNameIdentifierOwner {
    String getRoutineKind();
    String getRoutineName();
    boolean isPrivateMethod();
}
