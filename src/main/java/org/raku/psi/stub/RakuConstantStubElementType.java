package org.raku.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import org.raku.RakuLanguage;
import org.raku.psi.RakuConstant;
import org.raku.psi.impl.RakuConstantImpl;
import org.raku.psi.stub.impl.RakuConstantStubImpl;
import org.raku.psi.stub.index.RakuStubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RakuConstantStubElementType extends IStubElementType<RakuConstantStub, RakuConstant> {
    public RakuConstantStubElementType() {
        super("CONSTANT", RakuLanguage.getInstance());
    }

    @Override
    public RakuConstant createPsi(@NotNull RakuConstantStub stub) {
        return new RakuConstantImpl(stub, this);
    }

    @NotNull
    @Override
    public RakuConstantStub createStub(@NotNull RakuConstant psi, StubElement parentStub) {
        return new RakuConstantStubImpl(parentStub, psi.getConstantName(), psi.isExported());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "rakuidea.stub.constant";
    }

    @Override
    public void serialize(@NotNull RakuConstantStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getConstantName());
        dataStream.writeBoolean(stub.isExported());
    }

    @NotNull
    @Override
    public RakuConstantStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef constantNameRef = dataStream.readName();
        boolean isExport = dataStream.readBoolean();
        return new RakuConstantStubImpl(parentStub, constantNameRef == null ? null : constantNameRef.getString(), isExport);
    }

    @Override
    public void indexStub(@NotNull RakuConstantStub stub, @NotNull IndexSink sink) {
        sink.occurrence(RakuStubIndexKeys.ALL_CONSTANTS, stub.getConstantName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return ((RakuConstant)node.getPsi()).getConstantName() != null;
    }
}
