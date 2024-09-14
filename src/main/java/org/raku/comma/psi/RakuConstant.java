package org.raku.comma.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.comma.psi.stub.RakuConstantStub;
import org.raku.comma.psi.symbols.RakuLexicalSymbolContributor;
import org.jetbrains.annotations.Nullable;

public interface RakuConstant extends RakuPsiDeclaration, StubBasedPsiElement<RakuConstantStub>,
                                      RakuLexicalSymbolContributor {
    @Nullable
    String getConstantName();
}
