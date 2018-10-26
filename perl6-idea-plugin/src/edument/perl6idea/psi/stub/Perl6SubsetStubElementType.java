package edument.perl6idea.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6Subset;
import edument.perl6idea.psi.impl.Perl6SubsetImpl;
import edument.perl6idea.psi.stub.impl.Perl6SubsetStubImpl;
import edument.perl6idea.psi.stub.index.Perl6StubIndexKeys;
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
        return new Perl6SubsetStubImpl(parentStub, psi.getSubsetName(), psi.isExported(), psi.getSubsetBaseTypeName());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.subset";
    }

    @Override
    public void serialize(@NotNull Perl6SubsetStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getTypeName());
        dataStream.writeBoolean(stub.isExported());
        dataStream.writeName(stub.getSubsetBaseTypeName());
    }

    @NotNull
    @Override
    public Perl6SubsetStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef subsetNameRef = dataStream.readName();
        boolean exported = dataStream.readBoolean();
        StringRef subsetBaseRef = dataStream.readName();
        return new Perl6SubsetStubImpl(parentStub, subsetNameRef.getString(), exported, subsetBaseRef.getString());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        String subsetName = ((Perl6Subset) node.getPsi()).getSubsetName();
        return subsetName != null && !subsetName.equals("<anon>");
    }

    @Override
    public void indexStub(@NotNull Perl6SubsetStub stub, @NotNull IndexSink sink) {
        String globalName = stub.getGlobalName();
        if (globalName != null) {
            sink.occurrence(Perl6StubIndexKeys.GLOBAL_TYPES, globalName);
        }
        else {
            String lexicalName = stub.getLexicalName();
            if (lexicalName != null)
                sink.occurrence(Perl6StubIndexKeys.LEXICAL_TYPES, lexicalName);
        }
    }
}
