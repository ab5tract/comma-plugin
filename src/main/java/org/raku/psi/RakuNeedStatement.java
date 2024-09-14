package org.raku.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.psi.stub.RakuNeedStatementStub;
import org.raku.psi.symbols.RakuLexicalSymbolContributor;

import java.util.List;

public interface RakuNeedStatement extends RakuLexicalSymbolContributor, StubBasedPsiElement<RakuNeedStatementStub> {
    List<String> getModuleNames();
}
