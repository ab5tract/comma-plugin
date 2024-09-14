package org.raku.comma.descriptors.surrounder;

import com.intellij.psi.PsiElement;

public abstract class RakuStringQuotesSurrounder<T extends PsiElement> extends RakuSurrounder<T> {
    public RakuStringQuotesSurrounder(boolean isStatement) {
        super(isStatement);
    }
}
