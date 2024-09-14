package org.raku.filetypes;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.RakuIcons;
import org.raku.RakuLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@InternalIgnoreDependencyViolation
public class RakuPodFileType extends LanguageFileType implements RakuMultiExtensionFileType {
    public static final RakuPodFileType INSTANCE = new RakuPodFileType();

    private RakuPodFileType() {
        super(RakuLanguage.getInstance());
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
        return "rakudoc";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return RakuIcons.CAMELIA;
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
