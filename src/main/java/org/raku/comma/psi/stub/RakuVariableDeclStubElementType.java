package org.raku.comma.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import com.intellij.util.ArrayUtil;
import com.intellij.util.io.StringRef;
import org.raku.comma.RakuLanguage;
import org.raku.comma.psi.RakuVariable;
import org.raku.comma.psi.RakuVariableDecl;
import org.raku.comma.psi.impl.RakuVariableDeclImpl;
import org.raku.comma.psi.stub.impl.RakuVariableDeclStubImpl;
import org.raku.comma.psi.stub.index.RakuStubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RakuVariableDeclStubElementType extends IStubElementType<RakuVariableDeclStub, RakuVariableDecl> {
    public RakuVariableDeclStubElementType() {
        super("VARIABLE_DECLARATION", RakuLanguage.INSTANCE);
    }

    @Override
    public RakuVariableDecl createPsi(@NotNull RakuVariableDeclStub stub) {
        return new RakuVariableDeclImpl(stub, this);
    }

    @NotNull
    @Override
    public RakuVariableDeclStub createStub(@NotNull RakuVariableDecl psi, StubElement parentStub) {
        return new RakuVariableDeclStubImpl(parentStub, psi.getVariableNames(), psi.inferType().getName(), psi.isExported());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "raku.stub.variableDeclaration";
    }

    @Override
    public void serialize(@NotNull RakuVariableDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        // We might have an arbitrary number of names declared, so save a counter too
        String[] names = stub.getVariableNames();
        dataStream.writeInt(names.length);
        for (String name : names)
            dataStream.writeName(name);
        dataStream.writeName(stub.getVariableType());
        dataStream.writeBoolean(stub.isExported());
    }

    @NotNull
    @Override
    public RakuVariableDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        int numberOfNames = dataStream.readInt();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < numberOfNames; i++)
            names.add(dataStream.readName().getString());
        StringRef variableTypeRef = dataStream.readName();
        String type = variableTypeRef == null ? null : variableTypeRef.getString();
        boolean exported = dataStream.readBoolean();
        return new RakuVariableDeclStubImpl(parentStub, ArrayUtil.toStringArray(names), type, exported);
    }

    @Override
    public void indexStub(@NotNull RakuVariableDeclStub stub, @NotNull IndexSink sink) {
        for (String name : stub.getVariableNames()) {
            if (RakuVariable.getTwigil(name) == '*') {
                sink.occurrence(RakuStubIndexKeys.DYNAMIC_VARIABLES, name);
            } else {
                sink.occurrence(RakuStubIndexKeys.ALL_ATTRIBUTES, name);
            }
        }
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        RakuVariableDecl variableDecl = (RakuVariableDecl) node.getPsi();
        // Maybe it is dynamic, then we need to stub it
        for (String name : variableDecl.getVariableNames()) {
            if (RakuVariable.getTwigil(name) == '*')
                return true;
        }
        // Attributes are stubbed as well
        String scope = variableDecl.getScope();
        return scope.equals("has") ||
                scope.equals("our") && variableDecl.isExported();
    }
}
