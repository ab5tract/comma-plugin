package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6TraitStub;
import edument.perl6idea.psi.symbols.Perl6SymbolContributor;

public interface Perl6Trait extends StubBasedPsiElement<Perl6TraitStub> {
    String getTraitModifier();
    String getTraitName();
}
