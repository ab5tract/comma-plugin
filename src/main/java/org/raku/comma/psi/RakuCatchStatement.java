package org.raku.comma.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface RakuCatchStatement extends RakuPsiElement, RakuControl, RakuTopicalizer {
    @Nullable
    @Override
    default PsiElement getTopic() {
        return null;
    }
}
