package org.raku.filetypes;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.RakuIcons;
import org.raku.RakuLanguage;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@InternalIgnoreDependencyViolation
public class RakuScriptFileType extends LanguageFileType implements RakuMultiExtensionFileType {
    public static final RakuScriptFileType INSTANCE = new RakuScriptFileType();

    private RakuScriptFileType() {
        super(RakuLanguage.getInstance());
    }

    @NotNull
    @Override
    public String getName() {
        return "Raku Script";
    }

    @Nls
    @Override
    public @NotNull String getDisplayName() {
        return getName();
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Raku script";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "rakuidea";
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
        return new String[]{"p6", "pl6", "rakuidea"};
    }
}
