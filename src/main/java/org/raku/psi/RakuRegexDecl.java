package org.raku.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.psi.stub.RakuRegexDeclStub;
import org.raku.psi.symbols.RakuLexicalSymbolContributor;
import org.raku.psi.symbols.RakuMOPSymbolContributor;

public interface RakuRegexDecl extends RakuPsiScope, RakuPsiDeclaration,
                                       StubBasedPsiElement<RakuRegexDeclStub>,
                                       RakuSignatureHolder, RakuLexicalSymbolContributor,
                                       RakuMOPSymbolContributor {
    String getRegexKind();
    String getRegexName();
    @Override
    String getMultiness();
}
