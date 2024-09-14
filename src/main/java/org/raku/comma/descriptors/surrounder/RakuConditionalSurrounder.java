package org.raku.comma.descriptors.surrounder;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuConditional;
import org.raku.comma.psi.RakuConditionalBranch;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuStatementList;

public abstract class RakuConditionalSurrounder<T extends RakuConditional> extends RakuSurrounder<T> {
    public RakuConditionalSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected PsiElement insertStatements(T surrounder, PsiElement[] statements) {
        RakuConditionalBranch[] branches = surrounder.getBranches();
        for (RakuConditionalBranch branch : branches) {
            RakuStatementList list = PsiTreeUtil.findChildOfType(branch.block, RakuStatementList.class);
            if (list != null) {
                list.add(RakuElementFactory.createNewLine(list.getProject()));
                list.addRange(statements[0], statements[statements.length - 1]);
                list.add(RakuElementFactory.createNewLine(list.getProject()));
            }
        }
        return null;
    }

    @Override
    protected PsiElement getAnchor(T surrounder) {
        return surrounder.getBranches()[0].condition;
    }
}
