package org.raku.comma.nqp;

import com.intellij.lang.Language;

public class NqpLanguage extends Language {
    public static NqpLanguage INSTANCE = new NqpLanguage();

    private NqpLanguage() {
        super("NQP");
    }
}
