package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6TypeName;
import edument.perl6idea.psi.impl.Perl6TypeNameImpl;
import edument.perl6idea.psi.stub.impl.Perl6TypeNameStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Perl6TypeNameStubElementType extends IStubElementType<Perl6TypeNameStub, Perl6TypeName> {
    public Perl6TypeNameStubElementType() {
        super("TYPE_NAME", Perl6Language.INSTANCE);
    }

    @Override
    public Perl6TypeName createPsi(@NotNull Perl6TypeNameStub stub) {
        return new Perl6TypeNameImpl(stub, this);
    }

    @NotNull
    @Override
    public Perl6TypeNameStub createStub(@NotNull Perl6TypeName psi, StubElement parentStub) {
        return new Perl6TypeNameStubImpl(parentStub, psi.getTypeName());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.typeName";
    }

    @Override
    public void serialize(@NotNull Perl6TypeNameStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getTypeName());
    }

    @NotNull
    @Override
    public Perl6TypeNameStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef typename = dataStream.readName();
        return new Perl6TypeNameStubImpl(parentStub, typename.toString());
    }

    @Override
    public void indexStub(@NotNull Perl6TypeNameStub stub, @NotNull IndexSink sink) {

    }
}
