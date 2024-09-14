package org.raku.psi;

import org.raku.psi.type.RakuType;

public interface RakuForStatement extends RakuPsiElement, RakuExtractable, RakuControl, RakuTopicalizer {
    RakuPsiElement getSource();
    RakuType inferLoopParameterType(int index);
}
