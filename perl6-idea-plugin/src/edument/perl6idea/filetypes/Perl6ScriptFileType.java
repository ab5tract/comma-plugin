package edument.perl6idea.filetypes;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.Perl6Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6ScriptFileType extends LanguageFileType implements RakuMultiExtensionFileType {
    public static final Perl6ScriptFileType INSTANCE = new Perl6ScriptFileType();

    private Perl6ScriptFileType() {
        super(Perl6Language.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Raku Script";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Raku script";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "p6";
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

    @Override
    public String[] getExtensions() {
        return new String[]{"p6", "pl6", "raku"};
    }
}
