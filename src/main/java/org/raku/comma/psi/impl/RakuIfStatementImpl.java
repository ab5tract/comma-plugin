package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuIfStatement;
import org.raku.comma.psi.RakuPointyBlock;
import org.raku.comma.psi.RakuPsiElement;
import org.raku.comma.psi.type.RakuType;
import org.jetbrains.annotations.NotNull;

import static org.raku.comma.parsing.RakuTokenTypes.STATEMENT_CONTROL;

public class RakuIfStatementImpl extends RakuASTWrapperPsiElement implements RakuIfStatement {
    public RakuIfStatementImpl(@NotNull ASTNode node) {
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
               PsiTreeUtil.getChildOfType(this, RakuPointyBlock.class) == null;
    }

    @Override
    public RakuType inferTopicType() {
        // Condition is first non-token thing.
        RakuPsiElement condition = PsiTreeUtil.getChildOfType(this, RakuPsiElement.class);
        return condition == null ? RakuIfStatement.super.inferTopicType() : condition.inferType();
    }
}
