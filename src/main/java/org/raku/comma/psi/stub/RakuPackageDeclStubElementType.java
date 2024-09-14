package org.raku.comma.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import org.raku.comma.RakuLanguage;
import org.raku.comma.psi.RakuPackageDecl;
import org.raku.comma.psi.impl.RakuPackageDeclImpl;
import org.raku.comma.psi.stub.impl.RakuPackageDeclStubImpl;
import org.raku.comma.psi.stub.index.RakuStubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RakuPackageDeclStubElementType extends IStubElementType<RakuPackageDeclStub, RakuPackageDecl> {
    public RakuPackageDeclStubElementType() {
        super("PACKAGE_DECLARATION", RakuLanguage.getInstance());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return ((RakuPackageDecl)node.getPsi()).getPackageName() != null;
    }

    @Override
    public RakuPackageDecl createPsi(@NotNull RakuPackageDeclStub stub) {
        return new RakuPackageDeclImpl(stub, this);
    }

    @NotNull
    @Override
    public RakuPackageDeclStub createStub(@NotNull RakuPackageDecl psi, StubElement parentStub) {
        return new RakuPackageDeclStubImpl(parentStub, psi.getPackageKind(), psi.getPackageName(), psi.isExported());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "raku.stub.packageDeclaration";
    }

    @Override
    public void serialize(@NotNull RakuPackageDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getPackageKind());
        dataStream.writeName(stub.getTypeName());
        dataStream.writeBoolean(stub.isExported());
    }

    @NotNull
    @Override
    public RakuPackageDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef packageKindRef = dataStream.readName();
        StringRef packageNameRef = dataStream.readName();
        boolean exported = dataStream.readBoolean();
        return new RakuPackageDeclStubImpl(parentStub, packageKindRef.getString(), packageNameRef.getString(), exported);
    }

    @Override
    public void indexStub(@NotNull RakuPackageDeclStub stub, @NotNull IndexSink sink) {
        String globalName = stub.getGlobalName();
        if (globalName != null) {
            sink.occurrence(RakuStubIndexKeys.GLOBAL_TYPES, globalName);
        }
        else {
            String lexicalName = stub.getLexicalName();
            if (lexicalName != null)
                sink.occurrence(RakuStubIndexKeys.LEXICAL_TYPES, lexicalName);
        }
    }
}
