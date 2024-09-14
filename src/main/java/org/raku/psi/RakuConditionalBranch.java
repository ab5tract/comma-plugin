package org.raku.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public class RakuConditionalBranch {
    public PsiElement term;
    @Nullable
    public PsiElement condition;
    @Nullable
    public RakuBlock block;

    public RakuConditionalBranch(PsiElement term, @Nullable PsiElement condition, @Nullable RakuBlock block) {
        this.term = term;
        this.condition = condition;
        this.block = block;
    }
}
