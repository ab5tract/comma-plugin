package org.raku.comma.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import org.raku.comma.RakuLanguage;
import org.raku.comma.psi.RakuRegexDecl;
import org.raku.comma.psi.impl.RakuRegexDeclImpl;
import org.raku.comma.psi.stub.impl.RakuRegexDeclStubImpl;
import org.raku.comma.psi.stub.index.RakuStubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RakuRegexDeclStubElementType extends IStubElementType<RakuRegexDeclStub, RakuRegexDecl> {
    public RakuRegexDeclStubElementType() {
        super("REGEX_DECLARATION", RakuLanguage.INSTANCE);
    }

    @Override
    public RakuRegexDecl createPsi(@NotNull RakuRegexDeclStub stub) {
        return new RakuRegexDeclImpl(stub, this);
    }

    @NotNull
    @Override
    public RakuRegexDeclStub createStub(@NotNull RakuRegexDecl psi, StubElement parentStub) {
        return new RakuRegexDeclStubImpl(parentStub, psi.getRegexName(), psi.isExported());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "raku.stub.regexDeclaration";
    }

    @Override
    public void serialize(@NotNull RakuRegexDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getRegexName());
        dataStream.writeBoolean(stub.isExported());
    }

    @NotNull
    @Override
    public RakuRegexDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef regexNameRef = dataStream.readName();
        boolean exported = dataStream.readBoolean();
        assert regexNameRef != null;
        return new RakuRegexDeclStubImpl(parentStub, regexNameRef.getString(), exported);
    }

    @Override
    public void indexStub(@NotNull RakuRegexDeclStub stub, @NotNull IndexSink sink) {
        sink.occurrence(RakuStubIndexKeys.ALL_REGEXES, stub.getRegexName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return !((RakuRegexDecl)node.getPsi()).getRegexName().equals("<anon>");
    }
}
