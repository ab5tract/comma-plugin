package org.raku.comma.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

public interface RakuHolder {
    default RakuStatement[] getElements() {
        if (!(this instanceof PsiElement))
            return new RakuStatement[0];

        RakuSemiList semiList = PsiTreeUtil.getChildOfType((PsiElement)this, RakuSemiList.class);
        if (semiList == null)
            return new RakuStatement[0];
        RakuStatement[] statements = PsiTreeUtil.getChildrenOfType(semiList, RakuStatement.class);
        return statements == null ? new RakuStatement[0] : statements;
    }
}
