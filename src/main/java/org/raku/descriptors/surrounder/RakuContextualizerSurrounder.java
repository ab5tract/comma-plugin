package org.raku.descriptors.surrounder;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuSemiList;

public abstract class RakuContextualizerSurrounder<T extends PsiElement> extends RakuSurrounder<T> {
    public RakuContextualizerSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected PsiElement insertStatements(T surrounder, PsiElement[] statements) {
        RakuSemiList semiList = PsiTreeUtil.getChildOfType(surrounder, RakuSemiList.class);
        if (semiList != null) {
            semiList.add(RakuElementFactory.createNewLine(semiList.getProject()));
            for (PsiElement statement : statements) semiList.add(statement.copy());
            semiList.add(RakuElementFactory.createNewLine(semiList.getProject()));
        }
        return null;
    }

    @Override
    protected PsiElement getAnchor(T surrounder) {
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
}
