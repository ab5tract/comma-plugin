package edument.perl6idea.formatter;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import edument.perl6idea.Perl6Language;

public class Perl6CodeStyleSettings extends CustomCodeStyleSettings {
    // Brace placement
    public static final String[] BRACE_PLACEMENT_OPTIONS = new String[] {"End of line", "Next line"};
    public static final int[] BRACE_PLACEMENT_VALUES = new int[]{ 1, 2 };

    // Brace related options
    public int PACKAGE_DECL_BRACE_STYLE = 1;
    public int ROUTINE_DECL_BRACE_STYLE = 1;
    public int REGEX_DECL_BRACE_STYLE = 1;
    public int PHASER_BRACE_STYLE = 1;
    public int OTHER_BRACE_STYLE = 1;

    // Keep in one line related options
    public boolean ROUTINES_DECLARATION_IN_ONE_LINE = true;
    public boolean REGEX_DECLARATION_IN_ONE_LINE = true;
    public boolean PACKAGE_DECLARATION_IN_ONE_LINE = true;
    public boolean POINTY_BLOCK_IN_ONE_LINE = true;

    // Spacing related options
    public boolean BEFORE_COMMA = false;
    public boolean AFTER_COMMA = true;
    public boolean BEFORE_FATARROW = true;
    public boolean AFTER_FATARROW = true;
    public boolean BEFORE_ASSIGNMENT = true;
    public boolean AFTER_ASSIGNMENT = true;
    public boolean BEFORE_INFIX = true;
    public boolean AFTER_INFIX = true;
    public boolean BEFORE_WHATEVER_STAR = false;
    public boolean AFTER_WHATEVER_STAR = false;
    public boolean AFTER_PREFIX_OPS = false;
    public boolean AFTER_LAMBDA = true;
    public boolean BEFORE_REGEX_INFIX_SPACING = true;
    public boolean AFTER_REGEX_INFIX_SPACING = true;
    public boolean BEFORE_REGEX_QUANTIFIER_SPACING = false;
    public boolean AFTER_REGEX_QUANTIFIER_SPACING = true;
    public boolean BEFORE_REGEX_SEPARATOR_SPACING = true;
    public boolean AFTER_REGEX_SEPARATOR_SPACING = true;
    public boolean CALL_PARENS_SPACING = false;
    public boolean GROUPING_PARENS_SPACING = false;
    public boolean ARRAY_LITERAL_PARENS_SPACING = false;
    public boolean REGEX_GROUP_PARENS_SPACING = false;
    public boolean REGEX_POSITIONAL_PARENS_SPACING = false;

    // Wrapping
    public boolean PARAMETER_WRAP = true;
    public boolean TRAIT_WRAP = true;
    public boolean CALL_ARGUMENTS_WRAP = true;
    public boolean ARRAY_ELEMENTS_WRAP = true;
    public boolean METHOD_CALL_WRAP = true;
    public boolean INFIX_APPLICATION_WRAP = false;

    // Alignment
    public boolean PARAMETER_ALIGNMENT = true;
    public boolean TRAIT_ALIGNMENT = true;
    public boolean CALL_ARGUMENTS_ALIGNMENT = false;
    public boolean ARRAY_ELEMENTS_ALIGNMENT = true;
    public boolean INFIX_APPLICATION_ALIGNMENT = false;

    // Customs
    public boolean CONVERT_TO_UNICODE = false;

    public Perl6CodeStyleSettings(CodeStyleSettings container) {
        super(Perl6Language.INSTANCE.getID(), container);
    }
}
