package org.raku.comma.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

/**
 * A common interface for language constructions that usually have a topic
 * and a block, e.g. `for $ {}`, `given $ {}`, `whenever $ {}` and so on.
 */
public interface RakuControl extends RakuPsiElement {
    @Nullable
    default PsiElement getTopic() {
        PsiElement block = getLastChild();
        if (!(block instanceof RakuBlock))
            return null;
        return ((RakuBlock)block).skipWhitespacesBackward();
    }

    @Nullable
    default RakuBlockoid getBlock() {
        PsiElement block = getLastChild();
        return block instanceof RakuBlock ? ((RakuBlock)block).getBlockoid()
                                          : block instanceof RakuBlockoid
                                             ? (RakuBlockoid)block
                                             : null;
    }

    default void addStatements(PsiElement[] statements) {
        RakuStatementList list = PsiTreeUtil.findChildOfType(getBlock(), RakuStatementList.class);
        if (list == null)
            return;
        list.add(RakuElementFactory.createNewLine(list.getProject()));
        for (PsiElement statement : statements) list.add(statement);
        list.add(RakuElementFactory.createNewLine(list.getProject()));
    }
}
