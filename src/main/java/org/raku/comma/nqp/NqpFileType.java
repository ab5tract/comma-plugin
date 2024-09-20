package org.raku.comma.nqp;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.raku.comma.nqp.support.NqpIcons;

import javax.swing.*;

public class NqpFileType extends LanguageFileType {

  public static final NqpFileType INSTANCE = new NqpFileType();

    protected NqpFileType() {
        super(NqpLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "NQP";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "Not (Quite) Perl";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "nqp";
    }

    @Override
    public Icon getIcon() {
        return NqpIcons.FILE;
    }
}
