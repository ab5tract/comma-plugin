package edument.perl6idea.filetypes;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.Perl6Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6PodFileType extends LanguageFileType implements RakuMultiExtensionFileType {
    public static final Perl6PodFileType INSTANCE = new Perl6PodFileType();

    private Perl6PodFileType() {
        super(Perl6Language.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Raku Pod";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Raku Pod (Plain Old Documentation)";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "pod6";
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
    public String getCharset(@NotNull VirtualFile file, byte @NotNull [] content) {
        return "UTF-8";
    }

    @Override
    public String[] getExtensions() {
        return new String[]{"pod6", "rakudoc"};
    }
}
