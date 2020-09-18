package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6PointyBlock;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.Perl6WithoutStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6WithoutStatementImpl extends ASTWrapperPsiElement implements Perl6WithoutStatement {
    public Perl6WithoutStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isTopicalizing() {
        return PsiTreeUtil.getChildOfType(this, Perl6PointyBlock.class) == null;
    }

    @Override
    public String inferTopicType() {
        // Condition is first non-token thing.
        Perl6PsiElement condition = PsiTreeUtil.getChildOfType(this, Perl6PsiElement.class);
        return condition == null ? Perl6WithoutStatement.super.inferTopicType() : condition.inferType();
    }
}
