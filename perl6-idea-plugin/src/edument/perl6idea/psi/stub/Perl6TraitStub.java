package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6Trait;

public interface Perl6TraitStub extends StubElement<Perl6Trait> {
    String getTraitModifier();
    String getTraitName();
}
