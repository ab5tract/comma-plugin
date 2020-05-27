package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6IfStatement;
import edument.perl6idea.psi.Perl6PointyBlock;
import edument.perl6idea.psi.Perl6PsiElement;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.STATEMENT_CONTROL;

public class Perl6IfStatementImpl extends ASTWrapperPsiElement implements Perl6IfStatement {
    public Perl6IfStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getLeadingStatementControl() {
        PsiElement control = findChildByType(STATEMENT_CONTROL);
        return control == null ? "" : control.getText();
    }

    @Override
    public boolean isTopicalizing() {
        return getLeadingStatementControl().equals("with") &&
                PsiTreeUtil.getChildOfType(this, Perl6PointyBlock.class) == null;
    }

    @Override
    public String inferTopicType() {
        // Condition is first non-token thing.
        Perl6PsiElement condition = PsiTreeUtil.getChildOfType(this, Perl6PsiElement.class);
        return condition == null ? Perl6IfStatement.super.inferTopicType() : condition.inferType();
    }
}
