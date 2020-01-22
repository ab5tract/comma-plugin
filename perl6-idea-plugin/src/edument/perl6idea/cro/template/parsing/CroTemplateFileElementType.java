package edument.perl6idea.cro.template.parsing;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.tree.IStubFileElementType;
import edument.perl6idea.cro.template.CroTemplateLanguage;
import edument.perl6idea.cro.template.psi.stub.CroTemplateFileStub;
import edument.perl6idea.cro.template.psi.stub.impl.CroTemplateFileStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CroTemplateFileElementType extends IStubFileElementType<CroTemplateFileStub> {
    public static final int STUB_VERSION = 28;

    public CroTemplateFileElementType() {
        super(CroTemplateLanguage.INSTANCE);
    }

    @Override
    public int getStubVersion() {
        return STUB_VERSION;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "croTemplate.stub.file";
    }

    @NotNull
    @Override
    public CroTemplateFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new CroTemplateFileStubImpl(null);
    }
}
