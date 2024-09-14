package org.raku.comma.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import org.raku.comma.psi.stub.RakuPackageDeclStub;
import org.raku.comma.psi.stub.index.RakuIndexableType;
import org.raku.comma.psi.symbols.RakuLexicalSymbolContributor;
import org.raku.comma.psi.symbols.RakuMOPSymbolContributor;
import org.raku.comma.psi.symbols.RakuSymbolCollector;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface RakuPackageDecl extends RakuPsiScope, RakuPsiDeclaration,
                                         StubBasedPsiElement<RakuPackageDeclStub>,
                                         RakuIndexableType, PsiNamedElement, RakuExtractable,
                                         RakuLexicalSymbolContributor, RakuMOPSymbolContributor {
    String getPackageKind();
    String getPackageName();
    boolean isStubbed();
    @Nullable
    PsiElement getPackageKeywordNode();
    void contributeNestedPackagesWithPrefix(RakuSymbolCollector collector, String prefix);
    List<RakuPackageDecl> collectChildren();
    List<RakuPackageDecl> collectParents();
    boolean trustsOthers();
    @Nullable
    RakuPackageDecl getMetaClass();
    void setMetaClass(RakuPackageDecl metaClass);
    RakuParameter[] getSignature();
}
