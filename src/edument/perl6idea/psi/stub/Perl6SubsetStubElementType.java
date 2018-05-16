package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6Subset;
import edument.perl6idea.psi.impl.Perl6SubsetImpl;
import edument.perl6idea.psi.stub.impl.Perl6SubsetStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Perl6SubsetStubElementType extends IStubElementType<Perl6SubsetStub, Perl6Subset> {
    public Perl6SubsetStubElementType() {
        super("SUBSET", Perl6Language.INSTANCE);
    }

    @Override
    public Perl6Subset createPsi(@NotNull Perl6SubsetStub stub) {
        return new Perl6SubsetImpl(stub, this);
    }

    @NotNull
    @Override
    public Perl6SubsetStub createStub(@NotNull Perl6Subset psi, StubElement parentStub) {
        return new Perl6SubsetStubImpl(parentStub, psi.getSubsetName());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.subset";
    }

    @Override
    public void serialize(@NotNull Perl6SubsetStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getSubsetName());
    }

    @NotNull
    @Override
    public Perl6SubsetStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef subsetNameRef = dataStream.readName();
        return new Perl6SubsetStubImpl(parentStub, subsetNameRef.getString());
    }

    @Override
    public void indexStub(@NotNull Perl6SubsetStub stub, @NotNull IndexSink sink) {

    }
}
