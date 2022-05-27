package edument.perl6idea.filetypes;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.Perl6Language;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@InternalIgnoreDependencyViolation
public class Perl6TestFileType extends LanguageFileType implements RakuMultiExtensionFileType {
    public static final Perl6TestFileType INSTANCE = new Perl6TestFileType();

    private Perl6TestFileType() {
        super(Perl6Language.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Raku Test";
    }

    @Nls
    @Override
    public @NotNull String getDisplayName() {
        return getName();
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Raku Test";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "rakutest";
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
        return new String[]{"t", "t6", "rakutest"};
    }
}
