package edument.perl6idea.cro.template;

import com.intellij.lang.Language;

public class CroTemplateLanguage extends Language {
    public static final CroTemplateLanguage INSTANCE = new CroTemplateLanguage();

    private CroTemplateLanguage() {
        super("CroTemplate");
    }
}
