package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuArrayComposer;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuSemiList;

public class RakuArraySurrounder extends RakuSurrounder<RakuArrayComposer> {
    public RakuArraySurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuArrayComposer createElement(Project project) {
        return RakuElementFactory.createArrayComposer(project);
    }

    @Override
    protected PsiElement insertStatements(RakuArrayComposer surrounder, PsiElement[] statements) {
        RakuSemiList semiList = PsiTreeUtil.getChildOfType(surrounder, RakuSemiList.class);
        if (semiList != null) {
            semiList.add(RakuElementFactory.createNewLine(semiList.getProject()));
            for (PsiElement statement : statements) semiList.add(statement.copy());
            semiList.add(RakuElementFactory.createNewLine(semiList.getProject()));
        }
        return null;
    }

    @Override
    protected PsiElement getAnchor(RakuArrayComposer surrounder) {
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
