package org.raku.psi.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import org.raku.RakuLanguage;
import org.raku.psi.RakuNeedStatement;
import org.raku.psi.impl.RakuNeedStatementImpl;
import org.raku.psi.stub.impl.RakuNeedStatementStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RakuNeedStatementStubElementType extends IStubElementType<RakuNeedStatementStub, RakuNeedStatement> {
    public RakuNeedStatementStubElementType() {
        super("NEED_STATEMENT", RakuLanguage.getInstance());
    }

    @Override
    public RakuNeedStatement createPsi(@NotNull RakuNeedStatementStub stub) {
        return new RakuNeedStatementImpl(stub, this);
    }

    @NotNull
    @Override
    public RakuNeedStatementStub createStub(@NotNull RakuNeedStatement psi, StubElement parentStub) {
        return new RakuNeedStatementStubImpl(parentStub, psi.getModuleNames());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "raku.stub.needStatement";
    }

    @Override
    public void serialize(@NotNull RakuNeedStatementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        List<String> names = stub.getModuleNames();
        dataStream.writeInt(names.size());
        for (String name : names)
            dataStream.writeName(name);
    }

    @NotNull
    @Override
    public RakuNeedStatementStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        int elems = dataStream.readInt();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < elems; i++) {
            StringRef ref = dataStream.readName();
            names.add(ref == null ? null : ref.getString());
        }
        return new RakuNeedStatementStubImpl(parentStub, names);
    }

    @Override
    public void indexStub(@NotNull RakuNeedStatementStub stub, @NotNull IndexSink sink) {
    }
}
