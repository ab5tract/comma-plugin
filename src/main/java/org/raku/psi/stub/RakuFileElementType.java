package org.raku.psi.stub;

import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import org.raku.RakuLanguage;
import org.raku.psi.stub.impl.RakuFileStubImpl;
import org.raku.psi.stub.index.RakuStubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RakuFileElementType extends IStubFileElementType<RakuFileStub> {
    public static final int STUB_VERSION = 29;

    public RakuFileElementType() {
        super(RakuLanguage.getInstance());
    }

    @Override
    public int getStubVersion() {
        return STUB_VERSION;
    }

    @Override
    public StubBuilder getBuilder() {
        return new RakuFileStubBuilder();
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "raku.stub.file";
    }

    @Override
    public void serialize(@NotNull final RakuFileStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getCompilationUnitName());
    }

    @NotNull
    @Override
    public RakuFileStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef compilationUnitName = dataStream.readName();
        return new RakuFileStubImpl(null, compilationUnitName == null ? null : compilationUnitName.getString());
    }

    @Override
    public void indexStub(@NotNull final RakuFileStub stub, @NotNull final IndexSink sink) {
        String compUnitName = stub.getCompilationUnitName();
        if (compUnitName != null)
            sink.occurrence(RakuStubIndexKeys.PROJECT_MODULES, compUnitName);
    }
}
