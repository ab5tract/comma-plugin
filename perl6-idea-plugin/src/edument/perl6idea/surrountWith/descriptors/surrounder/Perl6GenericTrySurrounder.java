package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;

public abstract class Perl6GenericTrySurrounder<T extends P6Control> extends Perl6Surrounder<T> {
    public Perl6GenericTrySurrounder(boolean isStatement) {
        super(isStatement);
    }

    protected abstract String createBranch();

    @Override
    protected T createElement(Project project) {
        T perl6Try = (T)Perl6ElementFactory.createTryStatement(project);
        Perl6CatchStatement catchBlock = Perl6ElementFactory.createCatchStatement(project);
        Perl6Statement statement = Perl6ElementFactory.createStatementFromText(project, createBranch());
        catchBlock.addStatements(new PsiElement[]{statement});
        perl6Try.addStatements(new PsiElement[]{catchBlock});
        return perl6Try;
    }

    @Override
    protected PsiElement insertStatements(T surrounder, PsiElement[] statements) {
        Perl6CatchStatement catchStatement = PsiTreeUtil.findChildOfType(surrounder, Perl6CatchStatement.class);
        Perl6StatementList statementList = PsiTreeUtil.findChildOfType(surrounder, Perl6StatementList.class);
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
