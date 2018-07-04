package edument.perl6idea.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6Trait;
import edument.perl6idea.psi.impl.Perl6TraitImpl;
import edument.perl6idea.psi.stub.impl.Perl6TraitStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Perl6TraitStubElementType extends IStubElementType<Perl6TraitStub, Perl6Trait> {
    public Perl6TraitStubElementType() {
        super("TRAIT", Perl6Language.INSTANCE);
    }

    @Override
    public Perl6Trait createPsi(@NotNull Perl6TraitStub stub) {
        return new Perl6TraitImpl(stub, this);
    }

    @NotNull
    @Override
    public Perl6TraitStub createStub(@NotNull Perl6Trait psi, StubElement parentStub) {
        return new Perl6TraitStubImpl(parentStub, psi.getTraitModifier(), psi.getTraitName());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.trait";
    }

    @Override
    public void serialize(@NotNull Perl6TraitStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getTraitName());
        dataStream.writeName(stub.getTraitModifier());
    }

    @NotNull
    @Override
    public Perl6TraitStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        StringRef modifier = dataStream.readName();
        return new Perl6TraitStubImpl(parentStub, modifier.getString(), name.getString());
    }

    @Override
    public void indexStub(@NotNull Perl6TraitStub stub, @NotNull IndexSink sink) {

    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        if (!(node instanceof Perl6Trait)) return false;
        String modifier = ((Perl6Trait)node).getTraitModifier();
        return modifier.equals("does") || modifier.equals("is");
    }
}
