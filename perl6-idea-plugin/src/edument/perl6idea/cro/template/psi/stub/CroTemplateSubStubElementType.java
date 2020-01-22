package edument.perl6idea.cro.template.psi.stub;

import com.intellij.psi.stubs.*;
import edument.perl6idea.cro.template.CroTemplateLanguage;
import edument.perl6idea.cro.template.psi.CroTemplateSub;
import edument.perl6idea.cro.template.psi.impl.CroTemplateSubImpl;
import edument.perl6idea.cro.template.psi.stub.impl.CroTemplateSubStubImpl;
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

    }
}
