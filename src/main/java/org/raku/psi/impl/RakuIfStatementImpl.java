package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuIfStatement;
import org.raku.psi.RakuPointyBlock;
import org.raku.psi.RakuPsiElement;
import org.raku.psi.type.RakuType;
import org.jetbrains.annotations.NotNull;

import static org.raku.parsing.RakuTokenTypes.STATEMENT_CONTROL;

public class RakuIfStatementImpl extends ASTWrapperPsiElement implements RakuIfStatement {
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
