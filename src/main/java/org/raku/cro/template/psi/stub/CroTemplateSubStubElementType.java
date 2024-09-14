package org.raku.cro.template.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import org.raku.cro.template.CroTemplateLanguage;
import org.raku.cro.template.psi.CroTemplateSub;
import org.raku.cro.template.psi.impl.CroTemplateSubImpl;
import org.raku.cro.template.psi.stub.impl.CroTemplateSubStubImpl;
import org.raku.cro.template.psi.stub.index.CroTemplateStubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CroTemplateSubStubElementType extends IStubElementType<CroTemplateSubStub, CroTemplateSub> {
    public CroTemplateSubStubElementType() {
        super("SUB", CroTemplateLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "croTemplate.stub.sub";
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return ((CroTemplateSub)node.getPsi()).getName() != null;
    }

    @Override
    public CroTemplateSub createPsi(@NotNull CroTemplateSubStub stub) {
        return new CroTemplateSubImpl(stub, this);
    }

    @NotNull
    @Override
    public CroTemplateSubStub createStub(@NotNull CroTemplateSub psi, StubElement parentStub) {
        return new CroTemplateSubStubImpl(parentStub, psi.getName());
    }

    @Override
    public void serialize(@NotNull CroTemplateSubStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeUTF(stub.getName());
    }

    @NotNull
    @Override
    public CroTemplateSubStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new CroTemplateSubStubImpl(parentStub, dataStream.readUTF());
    }

    @Override
    public void indexStub(@NotNull CroTemplateSubStub stub, @NotNull IndexSink sink) {
        sink.occurrence(CroTemplateStubIndexKeys.TEMPLATE_SUB, stub.getName());
    }
}
