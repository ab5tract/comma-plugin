package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6Trait;
import edument.perl6idea.psi.stub.Perl6TraitStub;

public class Perl6TraitStubImpl extends StubBase<Perl6Trait> implements Perl6TraitStub {
    private final String modifier;
    private final String name;

    public Perl6TraitStubImpl(StubElement parent, String modifier, String name) {
        super(parent, Perl6ElementTypes.TRAIT);
        this.modifier = modifier;
        this.name = name;
    }

    @Override
    public String getTraitModifier() {
        return modifier;
    }

    @Override
    public String getTraitName() {
        return name;
    }
}
