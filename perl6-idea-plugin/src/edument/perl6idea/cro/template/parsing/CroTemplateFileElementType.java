package edument.perl6idea.cro.template.parsing;

import com.intellij.psi.tree.IStubFileElementType;
import edument.perl6idea.cro.template.CroTemplateLanguage;
import edument.perl6idea.cro.template.psi.stub.CroTemplateFileStub;
import org.jetbrains.annotations.NotNull;

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
}
