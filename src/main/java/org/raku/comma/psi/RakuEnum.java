package org.raku.comma.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.comma.psi.stub.RakuEnumStub;
import org.raku.comma.psi.stub.index.RakuIndexableType;
import org.raku.comma.psi.symbols.RakuLexicalSymbolContributor;

import java.util.Collection;

public interface RakuEnum extends RakuPsiDeclaration, StubBasedPsiElement<RakuEnumStub>,
                                  RakuIndexableType, RakuLexicalSymbolContributor {
    String getEnumName();
    Collection<String> getEnumValues();
}
