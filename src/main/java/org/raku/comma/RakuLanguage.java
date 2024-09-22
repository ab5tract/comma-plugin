package org.raku.comma;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class RakuLanguage extends Language {
    private static final RakuLanguage INSTANCE = new RakuLanguage();

    private RakuLanguage() {
        super("Raku");
    }

    @NotNull @Override public String getDisplayName() {
        return "Raku";
    }
}
