package org.raku.comma.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import org.raku.comma.psi.stub.RakuRoutineDeclStub;
import org.raku.comma.psi.symbols.RakuLexicalSymbolContributor;
import org.raku.comma.psi.symbols.RakuMOPSymbolContributor;
import org.jetbrains.annotations.NotNull;

public interface RakuRoutineDecl extends RakuPsiScope, RakuPsiDeclaration,
                                         StubBasedPsiElement<RakuRoutineDeclStub>,
                                         RakuSignatureHolder, PsiNamedElement,
                                         RakuExtractable, RakuLexicalSymbolContributor,
                                         RakuMOPSymbolContributor, RakuDeprecatable {
    String getRoutineKind();
    String getRoutineName();
    boolean isPrivate();
    boolean isStubbed();
    @NotNull
    PsiElement[] getContent();
    RakuParameter[] getParams();
    PsiElement getDeclaratorNode();

    boolean isMethod();
    boolean isSub();
    boolean isPure();
}
