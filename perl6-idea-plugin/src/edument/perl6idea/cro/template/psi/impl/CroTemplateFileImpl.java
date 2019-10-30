package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import edument.perl6idea.cro.template.CroTemplateFileType;
import edument.perl6idea.cro.template.CroTemplateLanguage;
import edument.perl6idea.cro.template.psi.CroTemplateFile;
import org.jetbrains.annotations.NotNull;

public class CroTemplateFileImpl extends PsiFileBase implements CroTemplateFile {
    public CroTemplateFileImpl(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CroTemplateLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CroTemplateFileType.INSTANCE;
    }
}
