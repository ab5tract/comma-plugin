package org.raku.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.psi.stub.RakuUseStatementStub;
import org.raku.psi.symbols.RakuLexicalSymbolContributor;
import org.jetbrains.annotations.Nullable;

public interface RakuUseStatement extends RakuLexicalSymbolContributor, StubBasedPsiElement<RakuUseStatementStub> {
    @Nullable
    String getModuleName();
}
