package org.raku.comma.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.comma.psi.stub.RakuRegexDeclStub;
import org.raku.comma.psi.symbols.RakuLexicalSymbolContributor;
import org.raku.comma.psi.symbols.RakuMOPSymbolContributor;

public interface RakuRegexDecl extends RakuPsiScope, RakuPsiDeclaration,
                                       StubBasedPsiElement<RakuRegexDeclStub>,
                                       RakuSignatureHolder, RakuLexicalSymbolContributor,
                                       RakuMOPSymbolContributor {
    String getRegexKind();
    String getRegexName();
    @Override
    String getMultiness();
}
