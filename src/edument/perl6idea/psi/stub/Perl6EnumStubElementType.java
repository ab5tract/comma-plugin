package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6Enum;
import edument.perl6idea.psi.impl.Perl6EnumImpl;
import edument.perl6idea.psi.stub.impl.Perl6EnumStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Perl6EnumStubElementType extends IStubElementType<Perl6EnumStub, Perl6Enum> {
    public Perl6EnumStubElementType() {
        super("ENUM", Perl6Language.INSTANCE);
    }

    @Override
    public Perl6Enum createPsi(@NotNull Perl6EnumStub stub) {
        return new Perl6EnumImpl(stub, this);
    }

    @NotNull
    @Override
    public Perl6EnumStub createStub(@NotNull Perl6Enum psi, StubElement parentStub) {
        return new Perl6EnumStubImpl(parentStub, psi.getEnumName());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.enum";
    }

    @Override
    public void serialize(@NotNull Perl6EnumStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getEnumName());
    }

    @NotNull
    @Override
    public Perl6EnumStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef enumNameRef = dataStream.readName();
        return new Perl6EnumStubImpl(parentStub, enumNameRef.getString());
    }

    @Override
    public void indexStub(@NotNull Perl6EnumStub stub, @NotNull IndexSink sink) {

    }
}
