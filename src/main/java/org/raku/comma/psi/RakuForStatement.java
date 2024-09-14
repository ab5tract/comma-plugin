package org.raku.comma.psi;

import org.raku.comma.psi.type.RakuType;

public interface RakuForStatement extends RakuPsiElement, RakuExtractable, RakuControl, RakuTopicalizer {
    RakuPsiElement getSource();
    RakuType inferLoopParameterType(int index);
}
