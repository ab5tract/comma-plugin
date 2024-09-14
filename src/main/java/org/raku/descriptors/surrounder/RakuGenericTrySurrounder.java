package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.*;

public abstract class RakuGenericTrySurrounder<T extends RakuControl> extends RakuSurrounder<T> {
    public RakuGenericTrySurrounder(boolean isStatement) {
        super(isStatement);
    }

    protected abstract String createBranch();

    @Override
    protected T createElement(Project project) {
        T perl6Try = (T)RakuElementFactory.createTryStatement(project);
        RakuCatchStatement catchBlock = RakuElementFactory.createCatchStatement(project);
        RakuStatement statement = RakuElementFactory.createStatementFromText(project, createBranch());
        catchBlock.addStatements(new PsiElement[]{statement});
        perl6Try.addStatements(new PsiElement[]{catchBlock});
        return perl6Try;
    }

    @Override
    protected PsiElement insertStatements(T surrounder, PsiElement[] statements) {
        RakuCatchStatement catchStatement = PsiTreeUtil.findChildOfType(surrounder, RakuCatchStatement.class);
        RakuStatementList statementList = PsiTreeUtil.findChildOfType(surrounder, RakuStatementList.class);
        if (statementList == null || catchStatement == null)
            return null;
        for (PsiElement statement : statements) {
            statementList.addBefore(statement, catchStatement);
        }
        return null;
    }

    @Override
    protected PsiElement getAnchor(T surrounder) {
        return null;
    }

    @Override
    protected boolean isControl() {
        return false;
    }
}
