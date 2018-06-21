package edument.perl6idea.filetypes;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.Perl6Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6ModuleFileType extends LanguageFileType {
    public static final Perl6ModuleFileType INSTANCE = new Perl6ModuleFileType();

    private Perl6ModuleFileType() {
        super(Perl6Language.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Perl 6 Module";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Perl 6 Module";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "pm6";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Perl6Icons.CAMELIA;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Nullable
    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        return "UTF-8";
    }
}
