package org.raku.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import org.raku.RakuLanguage;
import org.raku.psi.RakuTrait;
import org.raku.psi.impl.RakuTraitImpl;
import org.raku.psi.stub.impl.RakuTraitStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RakuTraitStubElementType extends IStubElementType<RakuTraitStub, RakuTrait> {
    public RakuTraitStubElementType() {
        super("TRAIT", RakuLanguage.getInstance());
    }

    @Override
    public RakuTrait createPsi(@NotNull RakuTraitStub stub) {
        return new RakuTraitImpl(stub, this);
    }

    @NotNull
    @Override
    public RakuTraitStub createStub(@NotNull RakuTrait psi, StubElement parentStub) {
        return new RakuTraitStubImpl(parentStub, psi.getTraitModifier(), psi.getTraitName());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "raku.stub.trait";
    }

    @Override
    public void serialize(@NotNull RakuTraitStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getTraitName());
        dataStream.writeName(stub.getTraitModifier());
    }

    @NotNull
    @Override
    public RakuTraitStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        StringRef modifier = dataStream.readName();
        return new RakuTraitStubImpl(parentStub, modifier.getString(), name.getString());
    }

    @Override
    public void indexStub(@NotNull RakuTraitStub stub, @NotNull IndexSink sink) {

    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        PsiElement psi = node.getPsi();
        if (!(psi instanceof RakuTrait)) return false;
        String modifier = ((RakuTrait)psi).getTraitModifier();
        return modifier.equals("does") || modifier.equals("is");
    }
}
