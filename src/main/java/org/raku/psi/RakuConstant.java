package org.raku.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.psi.stub.RakuConstantStub;
import org.raku.psi.symbols.RakuLexicalSymbolContributor;
import org.jetbrains.annotations.Nullable;

public interface RakuConstant extends RakuPsiDeclaration, StubBasedPsiElement<RakuConstantStub>,
                                      RakuLexicalSymbolContributor {
    @Nullable
    String getConstantName();
}
