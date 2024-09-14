package org.raku.comma.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.comma.psi.stub.RakuSubCallStub;

public interface RakuSubCall extends RakuPsiElement, RakuCodeBlockCall, RakuExtractable,
                                     StubBasedPsiElement<RakuSubCallStub> {
    boolean maybeCoercion();
}
