package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6RegexAtom;
import edument.perl6idea.psi.Perl6RegexGroup;
import edument.perl6idea.psi.Perl6RegexPsiElement;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexGroupImpl extends ASTWrapperPsiElement implements Perl6RegexGroup {
    public Perl6RegexGroupImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean mightMatchZeroWidth() {
        return atomsMightMatchZeroWidth(PsiTreeUtil.getChildrenOfType(this, Perl6RegexAtom.class));
    }
}
