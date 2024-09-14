package org.raku.comma.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.comma.psi.stub.RakuUseStatementStub;
import org.raku.comma.psi.symbols.RakuLexicalSymbolContributor;
import org.jetbrains.annotations.Nullable;

public interface RakuUseStatement extends RakuLexicalSymbolContributor, StubBasedPsiElement<RakuUseStatementStub> {
    @Nullable
    String getModuleName();
}
