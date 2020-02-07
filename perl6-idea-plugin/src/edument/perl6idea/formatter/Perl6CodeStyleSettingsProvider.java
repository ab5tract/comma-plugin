package edument.perl6idea.formatter;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.*;
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
                                                "            redo A if $y++ ==\n" +
                                                "                    1;\n" +
                                                "            last A\n" +
                                                "        }\n" +
                                                "    }\n" +
                                                "    say $t;\n" +
                                                "    " +
                                                "    # OUTPUT: «A1B1A1A2»\n" +
                                                "}\n";
    private static final String BRACES_SAMPLE = "say 1 +\n" +
                                                "        41;\n" +
                                                "\n" +
                                                "class Foo {\n" +
                                                "    method foo {\n" +
                                                "        if True { say \"One-liner\" }\n" +
                                                "        else {}\n" +
                                                "\n" +
                                                "        for ^42 {\n" +
                                                "            LAST { say \"LAST\" }\n" +
                                                "        }\n" +
                                                "    }\n" +
                                                "\n" +
                                                "    method answer { -> { 42 } }\n" +
                                                "    method empty {}\n" +
                                                "\n" +
                                                "    class Empty {}\n" +
                                                "}\n" +
                                                "\n" +
                                                "grammar Bar {\n" +
                                                "    token a { <?> }\n" +
                                                "    token long {\n" +
                                                "        \\w \\w \\w \\w\n" +
                                                "    }\n" +
                                                "}";

    @Nullable
    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.INDENT_SETTINGS)
            return INDENT_SAMPLE;
        if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS)
            return BRACES_SAMPLE;
        return "";
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

    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
            consumer.showStandardOptions(
                // Hard wrap at, wrap on typing
                "RIGHT_MARGIN", "WRAP_ON_TYPING",
                // keeping lines
                "KEEP_LINE_BREAKS", "KEEP_FIRST_COLUMN_COMMENT", "KEEP_CONTROL_STATEMENT_IN_ONE_LINE",
                "KEEP_MULTIPLE_EXPRESSIONS_IN_ONE_LINE", "KEEP_SIMPLE_BLOCKS_IN_ONE_LINE"
            );
            consumer.showCustomOption(
                Perl6CodeStyleSettings.class,
                "ROUTINES_DECLARATION_IN_ONE_LINE", "Routine declaration in one line",
                CodeStyleSettingsCustomizable.WRAPPING_KEEP);
            consumer.showCustomOption(
                Perl6CodeStyleSettings.class,
                "REGEX_DECLARATION_IN_ONE_LINE", "Regex declaration in one line",
                CodeStyleSettingsCustomizable.WRAPPING_KEEP);
            consumer.showCustomOption(
                Perl6CodeStyleSettings.class,
                "CLASS_DECLARATION_IN_ONE_LINE", "Class declaration in one line",
                CodeStyleSettingsCustomizable.WRAPPING_KEEP);
            consumer.showCustomOption(
                Perl6CodeStyleSettings.class,
                "POINTY_BLOCK_IN_ONE_LINE", "Pointy block in one line",
                CodeStyleSettingsCustomizable.WRAPPING_KEEP);
            consumer.showCustomOption(
                Perl6CodeStyleSettings.class,
                "PACKAGE_DECL_BRACE_STYLE", "In package declaration",
                "Braces placement", CodeStyleSettingsCustomizable.BRACE_PLACEMENT_OPTIONS, CodeStyleSettingsCustomizable.BRACE_PLACEMENT_VALUES);
            consumer.showCustomOption(
                Perl6CodeStyleSettings.class,
                "ROUTINE_DECL_BRACE_STYLE", "In routine declaration",
                "Braces placement", CodeStyleSettingsCustomizable.BRACE_PLACEMENT_OPTIONS, CodeStyleSettingsCustomizable.BRACE_PLACEMENT_VALUES);
            consumer.showCustomOption(
                Perl6CodeStyleSettings.class,
                "REGEX_DECL_BRACE_STYLE", "In regex declaration",
                "Braces placement", CodeStyleSettingsCustomizable.BRACE_PLACEMENT_OPTIONS, CodeStyleSettingsCustomizable.BRACE_PLACEMENT_VALUES);
            consumer.showCustomOption(
                Perl6CodeStyleSettings.class,
                "PHASER_BRACE_STYLE", "In phasers",
                "Braces placement", CodeStyleSettingsCustomizable.BRACE_PLACEMENT_OPTIONS, CodeStyleSettingsCustomizable.BRACE_PLACEMENT_VALUES);
            consumer.showCustomOption(
                Perl6CodeStyleSettings.class,
                "OTHER_BRACE_STYLE", "Other",
                "Braces placement", CodeStyleSettingsCustomizable.BRACE_PLACEMENT_OPTIONS, CodeStyleSettingsCustomizable.BRACE_PLACEMENT_VALUES);
        }
    }

    @Nullable
    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new SmartIndentOptionsEditor();
    }

    @Nullable
    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new Perl6CodeStyleSettings(settings);
    }

    @Override
    protected void customizeDefaults(@NotNull CommonCodeStyleSettings commonSettings,
                                     @NotNull CommonCodeStyleSettings.IndentOptions indentOptions) {
        commonSettings.KEEP_LINE_BREAKS = true;
        commonSettings.KEEP_FIRST_COLUMN_COMMENT = true;
        commonSettings.KEEP_SIMPLE_BLOCKS_IN_ONE_LINE = true;
        commonSettings.KEEP_CONTROL_STATEMENT_IN_ONE_LINE = false;
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return Perl6Language.INSTANCE;
    }
}
