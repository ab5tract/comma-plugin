package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6TraitStub;

public interface Perl6Trait extends StubBasedPsiElement<Perl6TraitStub> {
    String getTraitModifier();
    String getTraitName();
}
