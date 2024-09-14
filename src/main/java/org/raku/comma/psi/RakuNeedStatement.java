package org.raku.comma.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.comma.psi.stub.RakuNeedStatementStub;
import org.raku.comma.psi.symbols.RakuLexicalSymbolContributor;

import java.util.List;

public interface RakuNeedStatement extends RakuLexicalSymbolContributor, StubBasedPsiElement<RakuNeedStatementStub> {
    List<String> getModuleNames();
}
