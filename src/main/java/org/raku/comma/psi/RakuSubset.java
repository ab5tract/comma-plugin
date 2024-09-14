package org.raku.comma.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.comma.psi.stub.RakuSubsetStub;
import org.raku.comma.psi.stub.index.RakuIndexableType;

public interface RakuSubset extends RakuPsiDeclaration, StubBasedPsiElement<RakuSubsetStub>,
                                    RakuIndexableType {
    String getSubsetName();
    RakuPackageDecl getSubsetBaseType();
    String getSubsetBaseTypeName();
}
