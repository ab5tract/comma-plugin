package org.raku.comma.formatter;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.codeStyle.*;
import org.raku.comma.RakuLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class RakuCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    private static final String INDENT_SAMPLE = """
        sub foobar($one,
                $two,
                $three) {
            my $x = 0;
            my $y = 0;
           \s
            my $t = '';
            A: while $x++ < 2 {
                $t ~= "A$x";
                B: while $y++ < 2 {
                    $t ~= "B$y";
                    redo A if $y++ ==
                            1;
                    last A
                }
            }
            say $t;
                # OUTPUT: «A1B1A1A2»
        }
        """;
    private static final String BRACES_SAMPLE = """
        say 123412341234 + 1234123412342;


        class Foo {
          method foo($aaa1, $aaa1, $aaa1) {
            if True { say "One-liner" }
            else {}

            for ^42 {
              LAST { say "LAST" }
            }
          }

          method answer is copy is copy is copy {
            -> {}
          }
          method empty {}

          class Empty {}
        }

        grammar Bar {
          token a {}
          token long {
            \\w \\w \\w \\w
          }
        }

        Foo.a-very-long-method-name(4242,4242).a-very-long-method-name(42424242);

        my $abc = [12341234, 12342134, 12342134];""";
    private static final String SPACING_SAMPLE = """
        my @a = 1, 2, 3, n => 42;

        my $bind := 'bind';

        1 + (2 * 3) / 4;

        @a.grep(* ~~ Int).grep(*.is-prime);

        for @a -> $item {
            $item.Str ~~ / (a && b) || [c d* e ** 42] /
        }

        say $bind.comb(2);

        my $array = [1, !True];
        my $hash = {:a, :b};
        """;

    @Nullable
    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.INDENT_SETTINGS) {
            return INDENT_SAMPLE;
        }
        if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
            return BRACES_SAMPLE;
        }
        if (settingsType == SettingsType.SPACING_SETTINGS) {
            return SPACING_SAMPLE;
        }
        return "";
    }

    @NotNull
    @Override
    public CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings baseSettings, @NotNull CodeStyleSettings modelSettings) {
        return new CodeStyleAbstractConfigurable(baseSettings, modelSettings, "Raku Code Style Settings") {
            @Override
            protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
                return new RakuCodeStylePanel(settings, modelSettings);
            }
        };
    }

    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
            consumer.showStandardOptions(
                "RIGHT_MARGIN", "WRAP_ON_TYPING", "KEEP_LINE_BREAKS", "KEEP_SIMPLE_BLOCKS_IN_ONE_LINE"
            );
            addKeeping(consumer, "ROUTINES_DECLARATION_IN_ONE_LINE", "Routine declaration in one line");
            addKeeping(consumer, "REGEX_DECLARATION_IN_ONE_LINE", "Regex declaration in one line");
            addKeeping(consumer, "PACKAGE_DECLARATION_IN_ONE_LINE", "Package declaration in one line");
            addKeeping(consumer, "POINTY_BLOCK_IN_ONE_LINE", "Pointy block in one line");
            addBracing(consumer, "PACKAGE_DECL_BRACE_STYLE", "In package declaration");
            addBracing(consumer, "ROUTINE_DECL_BRACE_STYLE", "In routine declaration");
            addBracing(consumer, "REGEX_DECL_BRACE_STYLE", "In regex declaration");
            addBracing(consumer, "PHASER_BRACE_STYLE", "In phasers");
            addBracing(consumer, "OTHER_BRACE_STYLE", "Other");
            addWrapRelated(consumer, "PARAMETER_WRAP", "Wrap", "Parameter list");
            addWrapRelated(consumer, "PARAMETER_ALIGNMENT", "Align", "Parameter list");
            addWrapRelated(consumer, "TRAIT_WRAP", "Wrap", "Traits");
            addWrapRelated(consumer, "TRAIT_ALIGNMENT", "Align", "Traits");
            addWrapRelated(consumer, "CALL_ARGUMENTS_WRAP", "Wrap", "Call arguments");
            addWrapRelated(consumer, "CALL_ARGUMENTS_ALIGNMENT", "Align", "Call arguments");
            addWrapRelated(consumer, "ARRAY_ELEMENTS_WRAP", "Wrap", "Array initializer");
            addWrapRelated(consumer, "ARRAY_ELEMENTS_ALIGNMENT", "Align", "Array initializer");
            addWrapRelated(consumer, "INFIX_APPLICATION_WRAP", "Wrap infix operands", "Other");
            addWrapRelated(consumer, "INFIX_APPLICATION_ALIGNMENT", "Align infix operands", "Other");
            addWrapRelated(consumer, "METHOD_CALL_WRAP", "Wrap method calls", "Other");
        }
        else if (settingsType == SettingsType.SPACING_SETTINGS) {
            addSpacing(consumer, "BEFORE_COMMA", "Before comma", "Other");
            addSpacing(consumer, "AFTER_COMMA", "After comma", "Other");
            addSpacing(consumer, "BEFORE_FATARROW", "Before fatarrow", "Other");
            addSpacing(consumer, "AFTER_FATARROW", "After fatarrow", "Other");
            addSpacing(consumer, "AFTER_LAMBDA", "After lambda", "Other");

            addSpacing(consumer, "BEFORE_ASSIGNMENT", "Before assign/bind (=, :=)", "Operators");
            addSpacing(consumer, "AFTER_ASSIGNMENT", "After assign/bind (=, :=)", "Operators");
            addSpacing(consumer, "BEFORE_INFIX", "Before infix", "Operators");
            addSpacing(consumer, "AFTER_INFIX", "After infix", "Operators");
            addSpacing(consumer, "BEFORE_WHATEVER_STAR", "Left of whenever star", "Operators");
            addSpacing(consumer, "AFTER_WHATEVER_STAR", "Right of whenever star", "Operators");
            addSpacing(consumer, "AFTER_PREFIX_OPS", "After prefix operator", "Operators");

            addSpacing(consumer, "BEFORE_REGEX_INFIX_SPACING", "Before infix (||)", "Regex");
            addSpacing(consumer, "AFTER_REGEX_INFIX_SPACING", "After infix (||)", "Regex");
            addSpacing(consumer, "BEFORE_REGEX_QUANTIFIER_SPACING", "Before quantifier (+, *, ?)", "Regex");
            addSpacing(consumer, "AFTER_REGEX_QUANTIFIER_SPACING", "After quantifier (+, *, ?)", "Regex");
            addSpacing(consumer, "BEFORE_REGEX_SEPARATOR_SPACING", "Before separator (**)", "Regex");
            addSpacing(consumer, "AFTER_REGEX_SEPARATOR_SPACING", "After separator (**)", "Regex");

            addSpacing(consumer, "CALL_PARENS_SPACING", "Call", "In-parentheses");
            addSpacing(consumer, "GROUPING_PARENS_SPACING", "Grouping", "In-parentheses");
            addSpacing(consumer, "ARRAY_LITERAL_PARENS_SPACING", "Array/List literal", "In-parentheses");
            addSpacing(consumer, "REGEX_GROUP_PARENS_SPACING", "Regex group", "In-parentheses");
            addSpacing(consumer, "REGEX_POSITIONAL_PARENS_SPACING", "Regex capture", "In-parentheses");
        }
    }

    private static void addSpacing(CodeStyleSettingsCustomizable consumer, String fieldName, String title, String groupName) {
        consumer.showCustomOption(RakuCodeStyleSettings.class, fieldName, title, groupName);
    }

    private static void addBracing(CodeStyleSettingsCustomizable consumer, String fieldName, String title) {
        consumer.showCustomOption(
          RakuCodeStyleSettings.class, fieldName, title,
          "Braces placement", RakuCodeStyleSettings.BRACE_PLACEMENT_OPTIONS, RakuCodeStyleSettings.BRACE_PLACEMENT_VALUES);
    }

    private static void addKeeping(CodeStyleSettingsCustomizable consumer, String fieldName, String title) {
        consumer.showCustomOption(RakuCodeStyleSettings.class, fieldName, title, "Keep when reformatting");
    }

    private static void addWrapRelated(CodeStyleSettingsCustomizable consumer, String fieldName, String title, String groupName) {
        consumer.showCustomOption(RakuCodeStyleSettings.class, fieldName, title, groupName);
    }

    @Nullable
    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new SmartIndentOptionsEditor();
    }

    @Nullable
    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new RakuCodeStyleSettings(settings);
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
        return RakuLanguage.INSTANCE;
    }
}
