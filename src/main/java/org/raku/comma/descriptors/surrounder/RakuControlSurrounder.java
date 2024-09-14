package org.raku.comma.descriptors.surrounder;

import com.intellij.psi.PsiElement;
import org.raku.comma.psi.RakuControl;

public abstract class RakuControlSurrounder<T extends RakuControl> extends RakuSurrounder<T> {
    public RakuControlSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected PsiElement insertStatements(T surrounder, PsiElement[] statements) {
        surrounder.addStatements(statements);
        return null;
    }

    @Override
    protected PsiElement getAnchor(T surrounder) {
        return surrounder.getTopic();
    }
}
