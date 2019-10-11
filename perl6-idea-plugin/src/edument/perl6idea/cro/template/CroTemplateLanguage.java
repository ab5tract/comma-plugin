// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.cro.template;

import com.intellij.lang.Language;

public class CroTemplateLanguage extends Language {
    public static final CroTemplateLanguage INSTANCE = new CroTemplateLanguage();

    private CroTemplateLanguage() {
        super("CroTemplate");
    }
}
