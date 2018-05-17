package edument.perl6idea.psi.stub;

import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.stub.impl.Perl6FileStubImpl;
import edument.perl6idea.psi.stub.index.Perl6StubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Perl6FileElementType extends IStubFileElementType<Perl6FileStub> {
    public static final int STUB_VERSION = 6;

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
        return "perl6.stub.file";
    }

    @Override
    public void serialize(@NotNull final Perl6FileStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getCompilationUnitName());
    }

    @NotNull
    @Override
    public Perl6FileStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef compilationUnitName = dataStream.readName();
        return new Perl6FileStubImpl(null, compilationUnitName == null ? null : compilationUnitName.getString());
    }

    @Override
    public void indexStub(@NotNull final Perl6FileStub stub, @NotNull final IndexSink sink) {
        String compUnitName = stub.getCompilationUnitName();
        if (compUnitName != null)
            sink.occurrence(Perl6StubIndexKeys.PROJECT_MODULES, compUnitName);
    }
}
