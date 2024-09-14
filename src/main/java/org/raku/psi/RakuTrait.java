package org.raku.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.raku.psi.stub.RakuTraitStub;
import org.jetbrains.annotations.Nullable;

public interface RakuTrait extends StubBasedPsiElement<RakuTraitStub>, RakuPsiElement {
    String getTraitModifier();
    String getTraitName();
    @Nullable
    RakuTypeName getCompositionTypeName();
    void changeTraitMod(String newMod);
}
