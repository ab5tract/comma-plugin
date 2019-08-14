package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6ArrayComposer;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6SemiList;

public class Perl6ArraySurrounder extends Perl6Surrounder<Perl6ArrayComposer> {
    public Perl6ArraySurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6ArrayComposer createElement(Project project) {
        return Perl6ElementFactory.createArrayComposer(project);
    }

    @Override
    protected void insertStatements(Perl6ArrayComposer surrounder, PsiElement[] statements) {
        Perl6SemiList semiList = PsiTreeUtil.getChildOfType(surrounder, Perl6SemiList.class);
        if (semiList != null) {
            for (PsiElement statement : statements) {
                semiList.add(statement.copy());
            }
        }
    }

    @Override
    protected PsiElement getAnchor(Perl6ArrayComposer surrounder) {
        return null;
    }

    @Override
    protected boolean isExpression() {
        return true;
    }

    @Override
    protected boolean isControl() {
        return false;
    }

    @Override
    public String getTemplateDescription() {
        return "[ ]";
    }
}
