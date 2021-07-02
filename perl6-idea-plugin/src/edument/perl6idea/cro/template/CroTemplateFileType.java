package edument.perl6idea.cro.template;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CroTemplateFileType extends LanguageFileType {
    public static final CroTemplateFileType INSTANCE = new CroTemplateFileType();

    private CroTemplateFileType() {
        super(CroTemplateLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Cro Template";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Cro::HTTP::WebApp template";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "crotmp";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Perl6Icons.CRO;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Nullable
    @Override
    public String getCharset(@NotNull VirtualFile file, byte @NotNull [] content) {
        return "UTF-8";
    }
}
