package edument.perl6idea.formatter;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import edument.perl6idea.Perl6Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6CodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    private static final String INDENT_SAMPLE = "sub foobar($one,\n" +
                                                "        $two,\n" +
                                                "        $three) {\n" +
                                                "    my $x = 0;\n" +
                                                "    my $y = 0;\n" +
                                                "    my $t = '';\n" +
                                                "    A: while $x++ < 2 {\n" +
                                                "        $t ~= \"A$x\";\n" +
                                                "        B: while $y++ < 2 {\n" +
                                                "            $t ~= \"B$y\";\n" +
                                                "            redo A if $y++ == 1;\n" +
                                                "            last A\n" +
                                                "        }\n" +
                                                "    }\n" +
                                                "    say $t; # OUTPUT: «A1B1A1A2»\n" +
                                                "}\n" +
                                                "    \n" +
                                                "foobar;";

    @Nullable
    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return INDENT_SAMPLE;
    }

    @NotNull
    @Override
    public CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings baseSettings, @NotNull CodeStyleSettings modelSettings) {
        return new CodeStyleAbstractConfigurable(baseSettings, modelSettings, "Raku Code Style Settings") {
            @Override
            protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
                return new CodeStylePerl6Panel(settings, modelSettings);
            }
        };
    }

    @Nullable
    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new Perl6IndentOptionsEditor();
    }


    @NotNull
    @Override
    public Language getLanguage() {
        return Perl6Language.INSTANCE;
    }
}
