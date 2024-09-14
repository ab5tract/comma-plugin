package org.raku.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import org.raku.psi.stub.RakuVariableDeclStub;
import org.raku.psi.symbols.RakuLexicalSymbolContributor;
import org.raku.psi.symbols.RakuMOPSymbolContributor;
import org.jetbrains.annotations.Nullable;

public interface RakuVariableDecl extends
                                  PsiNameIdentifierOwner, StubBasedPsiElement<RakuVariableDeclStub>,
                                  RakuLexicalSymbolContributor, RakuMOPSymbolContributor, RakuVariableSource {
    boolean hasInitializer();
    @Nullable
    PsiElement getInitializer(RakuVariable variable);
    @Nullable
    PsiElement getInitializer();
    void removeVariable(RakuVariable variable);
}
