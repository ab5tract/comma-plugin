package org.raku.comma.psi.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import org.raku.comma.RakuLanguage;
import org.raku.comma.psi.RakuTypeName;
import org.raku.comma.psi.impl.RakuTypeNameImpl;
import org.raku.comma.psi.stub.impl.RakuTypeNameStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RakuTypeNameStubElementType extends IStubElementType<RakuTypeNameStub, RakuTypeName> {
    public RakuTypeNameStubElementType() {
        super("TYPE_NAME", RakuLanguage.getInstance());
    }

    @Override
    public RakuTypeName createPsi(@NotNull RakuTypeNameStub stub) {
        return new RakuTypeNameImpl(stub, this);
    }

    @NotNull
    @Override
    public RakuTypeNameStub createStub(@NotNull RakuTypeName psi, StubElement parentStub) {
        return new RakuTypeNameStubImpl(parentStub, psi.getTypeName());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "raku.stub.typeName";
    }

    @Override
    public void serialize(@NotNull RakuTypeNameStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getTypeName());
    }

    @NotNull
    @Override
    public RakuTypeNameStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef typename = dataStream.readName();
        return new RakuTypeNameStubImpl(parentStub, typename.toString());
    }

    @Override
    public void indexStub(@NotNull RakuTypeNameStub stub, @NotNull IndexSink sink) {

    }
}
