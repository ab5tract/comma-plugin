package org.raku.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.psi.stub.RakuEnumStub;
import org.raku.psi.stub.index.RakuIndexableType;
import org.raku.psi.symbols.RakuLexicalSymbolContributor;

import java.util.Collection;

public interface RakuEnum extends RakuPsiDeclaration, StubBasedPsiElement<RakuEnumStub>,
                                  RakuIndexableType, RakuLexicalSymbolContributor {
    String getEnumName();
    Collection<String> getEnumValues();
}
