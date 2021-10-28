package edument.perl6idea.highlighter;

import com.intellij.psi.PsiElementVisitor;
import edument.perl6idea.psi.*;

public abstract class RakuElementVisitor extends PsiElementVisitor {
    public void visitRakuElement(Perl6PsiElement element) {
        if (element instanceof Perl6PsiScope) {
            visitScope((Perl6PsiScope)element);
        }
    }

    public abstract void visitScope(Perl6PsiScope element);
}
