package org.raku.psi;

import com.intellij.psi.util.PsiTreeUtil;

public interface RakuBlockOrHash extends RakuPsiElement, RakuPsiScope, RakuExtractable, RakuHolder, RakuControl {
    @Override
    default RakuStatement[] getElements() {
        RakuStatementList list = PsiTreeUtil.findChildOfType(this, RakuStatementList.class);
        RakuStatement[] statements = PsiTreeUtil.getChildrenOfType(list, RakuStatement.class);
        return statements == null ? new RakuStatement[0] : statements;
    }

    /* Would the block be interpreted as a hash? */
    boolean isHash();

    /* Does the block look like something that might have been intended as a hash,
     * but will actually be treated as a block? */
    boolean isHashish();
}
