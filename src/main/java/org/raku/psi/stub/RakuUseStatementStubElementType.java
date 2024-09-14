package org.raku.psi.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import org.raku.RakuLanguage;
import org.raku.psi.RakuUseStatement;
import org.raku.psi.impl.RakuUseStatementImpl;
import org.raku.psi.stub.impl.RakuUseStatementStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RakuUseStatementStubElementType extends IStubElementType<RakuUseStatementStub, RakuUseStatement> {
    public RakuUseStatementStubElementType() {
        super("USE_STATEMENT", RakuLanguage.getInstance());
    }

    @Override
    public RakuUseStatement createPsi(@NotNull RakuUseStatementStub stub) {
        return new RakuUseStatementImpl(stub, this);
    }

    @NotNull
    @Override
    public RakuUseStatementStub createStub(@NotNull RakuUseStatement psi, StubElement parentStub) {
        return new RakuUseStatementStubImpl(parentStub, psi.getModuleName());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "rakuidea.stub.useStatement";
    }

    @Override
    public void serialize(@NotNull RakuUseStatementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getModuleName());
    }

    @NotNull
    @Override
    public RakuUseStatementStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef ref = dataStream.readName();
        return new RakuUseStatementStubImpl(parentStub, ref == null ? null : ref.getString());
    }

    @Override
    public void indexStub(@NotNull RakuUseStatementStub stub, @NotNull IndexSink sink) {
    }
}
