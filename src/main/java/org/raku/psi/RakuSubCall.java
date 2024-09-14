package org.raku.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.psi.stub.RakuSubCallStub;

public interface RakuSubCall extends RakuPsiElement, RakuCodeBlockCall, RakuExtractable,
                                     StubBasedPsiElement<RakuSubCallStub> {
    boolean maybeCoercion();
}
