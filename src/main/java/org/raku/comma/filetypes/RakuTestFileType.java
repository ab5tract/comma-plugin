package org.raku.comma.filetypes;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.comma.RakuIcons;
import org.raku.comma.RakuLanguage;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@InternalIgnoreDependencyViolation
public class RakuTestFileType extends LanguageFileType implements RakuMultiExtensionFileType {
    public static final RakuTestFileType INSTANCE = new RakuTestFileType();

    private RakuTestFileType() {
        super(RakuLanguage.INSTANCE);
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
        return new String[]{"t", "t6", "rakutest"};
    }
}
