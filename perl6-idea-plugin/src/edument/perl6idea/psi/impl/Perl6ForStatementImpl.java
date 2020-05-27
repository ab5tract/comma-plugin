package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6ForStatement;
import edument.perl6idea.psi.Perl6PointyBlock;
import org.jetbrains.annotations.NotNull;

public class Perl6ForStatementImpl extends ASTWrapperPsiElement implements Perl6ForStatement {
    public Perl6ForStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isTopicalizing() {
        return PsiTreeUtil.getChildOfType(this, Perl6PointyBlock.class) == null;
    }
}
