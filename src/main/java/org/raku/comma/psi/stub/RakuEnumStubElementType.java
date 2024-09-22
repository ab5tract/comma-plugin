package org.raku.comma.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import org.raku.comma.RakuLanguage;
import org.raku.comma.psi.RakuEnum;
import org.raku.comma.psi.impl.RakuEnumImpl;
import org.raku.comma.psi.stub.impl.RakuEnumStubImpl;
import org.raku.comma.psi.stub.index.RakuStubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class RakuEnumStubElementType extends IStubElementType<RakuEnumStub, RakuEnum> {
    public RakuEnumStubElementType() {
        super("ENUM", RakuLanguage.INSTANCE);
    }

    @Override
    public RakuEnum createPsi(@NotNull RakuEnumStub stub) {
        return new RakuEnumImpl(stub, this);
    }

    @NotNull
    @Override
    public RakuEnumStub createStub(@NotNull RakuEnum psi, StubElement parentStub) {
        return new RakuEnumStubImpl(parentStub, psi.getEnumName(), psi.isExported(), psi.getEnumValues());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "raku.stub.enum";
    }

    @Override
    public void serialize(@NotNull RakuEnumStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getTypeName());
        dataStream.writeBoolean(stub.isExported());
        StringJoiner joiner = new StringJoiner("#");
        for (String type : stub.getEnumValues()) {
            joiner.add(type);
        }
        dataStream.writeName(joiner.toString());
    }

    @NotNull
    @Override
    public RakuEnumStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef enumNameRef = dataStream.readName();
        boolean exported = dataStream.readBoolean();
        StringRef values = dataStream.readName();
        List<String> enumValues = values == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(values.getString().split("#")));
        assert enumNameRef != null;
        return new RakuEnumStubImpl(parentStub, enumNameRef.getString(), exported, enumValues);
    }

    @Override
    public void indexStub(@NotNull RakuEnumStub stub, @NotNull IndexSink sink) {
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

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        PsiElement psi = node.getPsi();
        return psi instanceof RakuEnum && ((RakuEnum) psi).getEnumName() != null;
    }
}
