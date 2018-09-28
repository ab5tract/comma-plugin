package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6TraitStub;
import org.jetbrains.annotations.Nullable;

public interface Perl6Trait extends StubBasedPsiElement<Perl6TraitStub> {
    String getTraitModifier();
    String getTraitName();
    @Nullable
    Perl6TypeName getCompositionTypeName();
}
