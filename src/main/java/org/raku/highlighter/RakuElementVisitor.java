package org.raku.highlighter;

import com.intellij.psi.PsiElementVisitor;
import org.raku.psi.*;

public abstract class RakuElementVisitor extends PsiElementVisitor {
    public void visitRakuElement(RakuPsiElement element) {
        if (element instanceof RakuPsiScope) {
            visitScope((RakuPsiScope)element);
        }
    }

    public abstract void visitScope(RakuPsiScope element);
}
