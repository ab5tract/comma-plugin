package edument.perl6idea.formatter;

import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import edument.perl6idea.Perl6Language;

public class CodeStylePerl6Panel extends TabbedLanguageCodeStylePanel {
    public CodeStylePerl6Panel(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
        super(Perl6Language.INSTANCE, settings, originalSettings);
    }

    @Override
    protected void initTabs(CodeStyleSettings settings) {
        addIndentOptionsTab(settings);
        addWrappingAndBracesTab(settings);
        addSpacesTab(settings);
    }
}
