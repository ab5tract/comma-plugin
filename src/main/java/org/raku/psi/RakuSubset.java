package org.raku.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.psi.stub.RakuSubsetStub;
import org.raku.psi.stub.index.RakuIndexableType;

public interface RakuSubset extends RakuPsiDeclaration, StubBasedPsiElement<RakuSubsetStub>,
                                    RakuIndexableType {
    String getSubsetName();
    RakuPackageDecl getSubsetBaseType();
    String getSubsetBaseTypeName();
}
