package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6IfStatement;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.STATEMENT_CONTROL;

public class Perl6IfStatementImpl extends ASTWrapperPsiElement implements Perl6IfStatement {
    public Perl6IfStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getLeadingStatementControl() {
        PsiElement control = this.findChildByType(STATEMENT_CONTROL);
        return control == null ? "" : control.getText();
    }
}
