package edument.perl6idea.psi.stub;

import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.stub.impl.Perl6FileStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Perl6FileElementType extends IStubFileElementType<Perl6FileStub> {
    public static final int STUB_VERSION = 4;

    public Perl6FileElementType() {
        super(Perl6Language.INSTANCE);
    }

    @Override
    public int getStubVersion() {
        return STUB_VERSION;
    }

    @Override
    public StubBuilder getBuilder() {
        return new Perl6FileStubBuilder();
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.FILE";
    }

    @Override
    public void serialize(@NotNull final Perl6FileStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getCompilationUnitName());
    }

    @NotNull
    @Override
    public Perl6FileStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        String compilationUnitName = dataStream.readNameString();
        return new Perl6FileStubImpl(null, compilationUnitName);
    }

    @Override
    public void indexStub(@NotNull final PsiFileStub stub, @NotNull final IndexSink sink) {

    }
}
