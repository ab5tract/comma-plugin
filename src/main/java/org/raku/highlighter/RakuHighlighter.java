package org.raku.highlighter;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

/* We want to both try and respect people's existing settings for language
 * default colors, while also providing better highlights for various cases
 * too. We distinguish 9 main groups with foreground colors (including the
 * neutral color) that this file is broken up into, and the default color
 * schemes will have distinct colors for those. In some cases, we can get
 * that to fall naturally out of the IDEA defaults, and do so where we can. */
public final class RakuHighlighter {
    /* Map illegal syntax into the default coloring rule for that. */
    static final String BAD_CHARACTER_ID = "PERL6_BAD_CHARACTER";
    public static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
            BAD_CHARACTER_ID,
            HighlighterColors.BAD_CHARACTER
    );

    /* Neutral
     * *******
     * These are things we effectively don't highlight. We map them to
     * things that, in default themes, won't have any highlight either,
     * but should respect defaults selected by the user. We do override
     * them somewhat in the Dracula scheme to ensure they are neutral
     * there.
     */

    static final String ARRAY_INDEXER_ID = "PERL6_ARRAY_INDEXER";
    public static final TextAttributesKey ARRAY_INDEXER = TextAttributesKey.createTextAttributesKey(
            ARRAY_INDEXER_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String BLOCK_CURLY_BRACKETS_ID = "PERL6_BLOCK_CURLY_BRACKETS";
    public static final TextAttributesKey BLOCK_CURLY_BRACKETS = TextAttributesKey.createTextAttributesKey(
            BLOCK_CURLY_BRACKETS_ID,
            DefaultLanguageHighlighterColors.BRACES
    );

    static final String CAPTURE_TERM_ID = "PERL6_CAPTURE_TERM";
    public static final TextAttributesKey CAPTURE_TERM = TextAttributesKey.createTextAttributesKey(
            CAPTURE_TERM_ID,
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    static final String HASH_INDEXER_ID = "PERL6_HASH_INDEXER";
    public static final TextAttributesKey HASH_INDEXER = TextAttributesKey.createTextAttributesKey(
            HASH_INDEXER_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String LAMBDA_ID = "PERL6_LAMBDA";
    public static final TextAttributesKey LAMBDA = TextAttributesKey.createTextAttributesKey(
            LAMBDA_ID,
            DefaultLanguageHighlighterColors.BRACES
    );

    static final String NAMED_PARAMETER_SYNTAX_ID = "PERL6_NAMED_PARAMETER_SYNTAX";
    public static final TextAttributesKey NAMED_PARAMETER_SYNTAX = TextAttributesKey.createTextAttributesKey(
            NAMED_PARAMETER_SYNTAX_ID,
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    static final String ONLY_STAR_ID = "PERL6_ONLY_STAR";
    public static final TextAttributesKey ONLY_STAR = TextAttributesKey.createTextAttributesKey(
            ONLY_STAR_ID,
            DefaultLanguageHighlighterColors.BRACES
    );

    static final String PARENTHESES_ID = "PERL6_PARENTHESES";
    public static final TextAttributesKey PARENTHESES = TextAttributesKey.createTextAttributesKey(
            PARENTHESES_ID,
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    static final String PARAMETER_SEPARATOR_ID = "PERL6_PARAMETER_SEPARATOR";
    public static final TextAttributesKey PARAMETER_SEPARATOR = TextAttributesKey.createTextAttributesKey(
            PARAMETER_SEPARATOR_ID,
            DefaultLanguageHighlighterColors.COMMA
    );

    static final String REGEX_ASSERTION_ANGLE_ID = "PERL6_REGEX_ASSERTION_ANGLE";
    public static final TextAttributesKey REGEX_ASSERTION_ANGLE = TextAttributesKey.createTextAttributesKey(
            REGEX_ASSERTION_ANGLE_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String REGEX_GROUP_BRACKET_ID = "PERL6_REGEX_GROUP_BRACKET";
    public static final TextAttributesKey REGEX_GROUP_BRACKET = TextAttributesKey.createTextAttributesKey(
            REGEX_GROUP_BRACKET_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String REGEX_CCLASS_SYNTAX_ID = "PERL6_CCLASS_SYNTAX";
    public static final TextAttributesKey REGEX_CCLASS_SYNTAX = TextAttributesKey.createTextAttributesKey(
            REGEX_CCLASS_SYNTAX_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String RETURN_ARROW_ID = "PERL6_RETURN_ARROW";
    public static final TextAttributesKey RETURN_ARROW = TextAttributesKey.createTextAttributesKey(
            RETURN_ARROW_ID,
            DefaultLanguageHighlighterColors.COMMA
    );

    static final String STATEMENT_TERMINATOR_ID = "PERL6_STATEMENT_TERMINATOR";
    public static final TextAttributesKey STATEMENT_TERMINATOR = TextAttributesKey.createTextAttributesKey(
            STATEMENT_TERMINATOR_ID,
            DefaultLanguageHighlighterColors.SEMICOLON
    );

    static final String TYPE_COERCION_PARENTHESES_ID = "PERL6_TYPE_COERCION_PARENTHESES";
    public static final TextAttributesKey TYPE_COERCION_PARENTHESES = TextAttributesKey.createTextAttributesKey(
            TYPE_COERCION_PARENTHESES_ID,
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    static final String TERM_DECLARATION_BACKSLASH_ID = "PERL6_TERM_DECLARATION_BACKSLASH";
    public static final TextAttributesKey TERM_DECLARATION_BACKSLASH = TextAttributesKey.createTextAttributesKey(
            TERM_DECLARATION_BACKSLASH_ID,
            DefaultLanguageHighlighterColors.COMMA
    );

    /* Labels
     * ******
     * Just inherit the platform defaults for these.
     */

    static final String LABEL_NAME_ID = "PERL6_LABEL_NAME";
    public static final TextAttributesKey LABEL_NAME = TextAttributesKey.createTextAttributesKey(
        LABEL_NAME_ID,
        DefaultLanguageHighlighterColors.LABEL
    );

    static final String LABEL_COLON_ID = "PERL6_LABEL_COLON";
    public static final TextAttributesKey LABEL_COLON = TextAttributesKey.createTextAttributesKey(
        LABEL_COLON_ID,
        DefaultLanguageHighlighterColors.LABEL
    );

    /* Operators
     * *********
     * These map to operation symbol, which is by default not highlighted.
     * In Raku, operators are significant, and the term/infix parser
     * interlocking is an important part of the syntax model, allowing the
     * use of characters to unambiguously be operators and terms. Thus we
     * will map these to a color in the default schemes. We also treat
     * array/hash composers and contextualizers as operator colored
     * things.
     */

    static final String CONTEXTUALIZER_ID = "PERL6_CONTEXTUALIZER";
    public static final TextAttributesKey CONTEXTUALIZER = TextAttributesKey.createTextAttributesKey(
            CONTEXTUALIZER_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String ARRAY_COMPOSER_ID = "PERL6_ARRAY_COMPOSER";
    public static final TextAttributesKey ARRAY_COMPOSER = TextAttributesKey.createTextAttributesKey(
            ARRAY_COMPOSER_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String HASH_COMPOSER_ID = "PERL6_HASH_COMPOSER";
    public static final TextAttributesKey HASH_COMPOSER = TextAttributesKey.createTextAttributesKey(
            HASH_COMPOSER_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String INFIX_ID = "PERL6_INFIX";
    public static final TextAttributesKey INFIX = TextAttributesKey.createTextAttributesKey(
            INFIX_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String METAOP_ID = "PERL6_METAOP";
    public static final TextAttributesKey METAOP = TextAttributesKey.createTextAttributesKey(
            METAOP_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String PARAMETER_QUANTIFIER_ID = "PERL6_PARAMETER_QUANTIFIER";
    public static final TextAttributesKey PARAMETER_QUANTIFIER = TextAttributesKey.createTextAttributesKey(
            PARAMETER_QUANTIFIER_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String POSTFIX_ID = "PERL6_POSTFIX";
    public static final TextAttributesKey POSTFIX = TextAttributesKey.createTextAttributesKey(
            POSTFIX_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String PREFIX_ID = "PERL6_PREFIX";
    public static final TextAttributesKey PREFIX = TextAttributesKey.createTextAttributesKey(
            PREFIX_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String REGEX_ANCHOR_ID = "PERL6_REGEX_ANCHOR";
    public static final TextAttributesKey REGEX_ANCHOR = TextAttributesKey.createTextAttributesKey(
            REGEX_ANCHOR_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String REGEX_INFIX_ID = "PERL6_REGEX_INFIX";
    public static final TextAttributesKey REGEX_INFIX = TextAttributesKey.createTextAttributesKey(
            REGEX_INFIX_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String REGEX_LOOKAROUND_ID = "PERL6_REGEX_LOOKAROUND";
    public static final TextAttributesKey REGEX_LOOKAROUND = TextAttributesKey.createTextAttributesKey(
            REGEX_LOOKAROUND_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String REGEX_MOD_ID = "PERL6_REGEX_MOD";
    public static final TextAttributesKey REGEX_MOD = TextAttributesKey.createTextAttributesKey(
            REGEX_MOD_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String REGEX_QUANTIFIER_ID = "PERL6_REGEX_QUANTIFIER";
    public static final TextAttributesKey REGEX_QUANTIFIER = TextAttributesKey.createTextAttributesKey(
            REGEX_QUANTIFIER_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String TRANS_RANGE_ID = "PERL6_TRANS_RANGE";
    public static final TextAttributesKey TRANS_RANGE = TextAttributesKey.createTextAttributesKey(
            TRANS_RANGE_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    /* Keyword
     * *******
     * These map to the default keyword color.
     */

    static final String MULTI_DECLARATOR_ID = "PERL6_MULTI_DECLARATOR";
    public static final TextAttributesKey MULTI_DECLARATOR = TextAttributesKey.createTextAttributesKey(
            MULTI_DECLARATOR_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String PACKAGE_DECLARATOR_ID = "PERL6_PACKAGE_DECLARATOR";
    public static final TextAttributesKey PACKAGE_DECLARATOR = TextAttributesKey.createTextAttributesKey(
            PACKAGE_DECLARATOR_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String PHASER_ID = "PERL6_PHASER";
    public static final TextAttributesKey PHASER = TextAttributesKey.createTextAttributesKey(
            PHASER_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String ROUTINE_DECLARATOR_ID = "PERL6_ROUTINE_DECLARATOR";
    public static final TextAttributesKey ROUTINE_DECLARATOR = TextAttributesKey.createTextAttributesKey(
            ROUTINE_DECLARATOR_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String QUASI_ID = "PERL6_QUASI";
    public static final TextAttributesKey QUASI = TextAttributesKey.createTextAttributesKey(
      QUASI_ID,
      DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String SCOPE_DECLARATOR_ID = "PERL6_SCOPE_DECLARATOR";
    public static final TextAttributesKey SCOPE_DECLARATOR = TextAttributesKey.createTextAttributesKey(
            SCOPE_DECLARATOR_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String STATEMENT_CONTROL_ID = "PERL6_STATEMENT_CONTROL";
    public static final TextAttributesKey STATEMENT_CONTROL = TextAttributesKey.createTextAttributesKey(
            STATEMENT_CONTROL_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String STATEMENT_MOD_ID = "PERL6_STATEMENT_MOD";
    public static final TextAttributesKey STATEMENT_MOD = TextAttributesKey.createTextAttributesKey(
            STATEMENT_MOD_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String STATEMENT_PREFIX_ID = "PERL6_STATEMENT_PREFIX";
    public static final TextAttributesKey STATEMENT_PREFIX = TextAttributesKey.createTextAttributesKey(
            STATEMENT_PREFIX_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String TRAIT_ID = "PERL6_TRAIT";
    public static final TextAttributesKey TRAIT = TextAttributesKey.createTextAttributesKey(
            TRAIT_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String TYPE_DECLARATOR_ID = "PERL6_TYPE_DECLARATOR";
    public static final TextAttributesKey TYPE_DECLARATOR = TextAttributesKey.createTextAttributesKey(
            TYPE_DECLARATOR_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String WHERE_CONSTRAINT_ID = "PERL6_WHERE_CONSTRAINT";
    public static final TextAttributesKey WHERE_CONSTRAINT = TextAttributesKey.createTextAttributesKey(
            WHERE_CONSTRAINT_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    /* Comments
     * ********
     * These map to the default comment color.
     */

    static final String COMMENT_ID = "PERL6_COMMENT";
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(
            COMMENT_ID,
            DefaultLanguageHighlighterColors.LINE_COMMENT
    );

    static final String STUB_CODE_ID = "PERL6_STUB_CODE";
    public static final TextAttributesKey STUB_CODE = TextAttributesKey.createTextAttributesKey(
            STUB_CODE_ID,
            DefaultLanguageHighlighterColors.LINE_COMMENT
    );

    /* String Literal
     * ***************
     * These map to the default string literal color. This includes
     * various bits of quoting configuration syntax.
     */

    static final String PAIR_KEY_ID = "PERL6_PAIR_KEY";
    public static final TextAttributesKey PAIR_KEY = TextAttributesKey.createTextAttributesKey(
            PAIR_KEY_ID,
            DefaultLanguageHighlighterColors.STRING
    );

    static final String QUOTE_MOD_ID = "PERL6_QUOTE_MOD";
    public static final TextAttributesKey QUOTE_MOD = TextAttributesKey.createTextAttributesKey(
            QUOTE_MOD_ID,
            DefaultLanguageHighlighterColors.STRING
    );

    static final String QUOTE_PAIR_ID = "PERL6_QUOTE_PAIR";
    public static final TextAttributesKey QUOTE_PAIR = TextAttributesKey.createTextAttributesKey(
            QUOTE_PAIR_ID,
            DefaultLanguageHighlighterColors.STRING
    );

    static final String QUOTE_REGEX_ID = "PERL6_QUOTE_REGEX";
    public static final TextAttributesKey QUOTE_REGEX = TextAttributesKey.createTextAttributesKey(
            QUOTE_REGEX_ID,
            DefaultLanguageHighlighterColors.STRING
    );

    static final String STRING_LITERAL_QUOTE_ID = "PERL6_STRING_LITERAL_QUOTE";
    public static final TextAttributesKey STRING_LITERAL_QUOTE = TextAttributesKey.createTextAttributesKey(
            STRING_LITERAL_QUOTE_ID,
            DefaultLanguageHighlighterColors.STRING
    );

    static final String STRING_LITERAL_CHAR_ID = "PERL6_STRING_LITERAL_CHAR";
    public static final TextAttributesKey STRING_LITERAL_CHAR = TextAttributesKey.createTextAttributesKey(
            STRING_LITERAL_CHAR_ID,
            DefaultLanguageHighlighterColors.STRING
    );

    static final String TRANS_CHAR_ID = "PERL6_TRANS_CHAR";
    public static final TextAttributesKey TRANS_CHAR = TextAttributesKey.createTextAttributesKey(
            TRANS_CHAR_ID,
            DefaultLanguageHighlighterColors.STRING
    );

    /* Numeric Literal
     * ***************
     * These map to the default numeric literal color.
     */

    static final String NUMERIC_LITERAL_ID = "PERL6_NUMERIC_LITERAL";
    public static final TextAttributesKey NUMERIC_LITERAL = TextAttributesKey.createTextAttributesKey(
            NUMERIC_LITERAL_ID,
            DefaultLanguageHighlighterColors.NUMBER
    );

    static final String VERSION_ID = "PERL6_VERSION";
    public static final TextAttributesKey VERSION = TextAttributesKey.createTextAttributesKey(
            VERSION_ID,
            DefaultLanguageHighlighterColors.NUMBER
    );

    /* Callable
     * ********
     * These map to the function call and function delcaration colors. but
     * the Raku included schema overrides them.
     */

    static final String METHOD_CALL_NAME_ID = "PERL6_METHOD_CALL_NAME";
    public static final TextAttributesKey METHOD_CALL_NAME = TextAttributesKey.createTextAttributesKey(
            METHOD_CALL_NAME_ID,
            DefaultLanguageHighlighterColors.FUNCTION_CALL
    );

    static final String ROUTINE_NAME_ID = "PERL6_ROUTINE_NAME";
    public static final TextAttributesKey ROUTINE_NAME = TextAttributesKey.createTextAttributesKey(
            ROUTINE_NAME_ID,
            DefaultLanguageHighlighterColors.FUNCTION_DECLARATION
    );

    static final String SUB_CALL_NAME_ID = "PERL6_SUB_CALL_NAME";
    public static final TextAttributesKey SUB_CALL_NAME = TextAttributesKey.createTextAttributesKey(
            SUB_CALL_NAME_ID,
            DefaultLanguageHighlighterColors.FUNCTION_CALL
    );

    /* Variables
     * *********
     * Mark these up all as local variable. We map that in the color
     * schemes.
     */

    static final String NAMED_PARAMETER_NAME_ALIAS_ID = "PERL6_NAMED_PARAMETER_NAME_ALIAS";
    public static final TextAttributesKey NAMED_PARAMETER_NAME_ALIAS = TextAttributesKey.createTextAttributesKey(
            NAMED_PARAMETER_NAME_ALIAS_ID,
            DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    );

    static final String REGEX_CAPTURE_ID = "PERL6_REGEX_CAPTURE";
    public static final TextAttributesKey REGEX_CAPTURE = TextAttributesKey.createTextAttributesKey(
            REGEX_CAPTURE_ID,
            DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    );

    static final String SHAPE_DECLARATION_ID = "PERL6_SHAPE_DECLARATION";
    public static final TextAttributesKey SHAPE_DECLARATION = TextAttributesKey.createTextAttributesKey(
            SHAPE_DECLARATION_ID,
            DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    );

    static final String SELF_ID = "PERL6_SELF";
    public static final TextAttributesKey SELF = TextAttributesKey.createTextAttributesKey(
            SELF_ID,
            DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    );

    static final String VARIABLE_ID = "PERL6_VARIABLE";
    public static final TextAttributesKey VARIABLE = TextAttributesKey.createTextAttributesKey(
            VARIABLE_ID,
            DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    );

    /* Types and terms
     * ***************
     * Mark these up all as class names. We'll assign them a color
     * in the Raku scheme.
     */

    static final String TERM_ID = "PERL6_TERM";
    public static final TextAttributesKey TERM = TextAttributesKey.createTextAttributesKey(
            TERM_ID,
            DefaultLanguageHighlighterColors.CLASS_NAME
    );

    static final String TYPE_NAME_ID = "PERL6_TYPE_NAME";
    public static final TextAttributesKey TYPE_NAME = TextAttributesKey.createTextAttributesKey(
            TYPE_NAME_ID,
            DefaultLanguageHighlighterColors.CLASS_NAME
    );

    static final String TYPE_PARAMETER_BRACKET_ID = "PERL6_TYPE_PARAMETER_BRACKET";
    public static final TextAttributesKey TYPE_PARAMETER_BRACKET = TextAttributesKey.createTextAttributesKey(
            TYPE_PARAMETER_BRACKET_ID,
            DefaultLanguageHighlighterColors.CLASS_NAME
    );

    static final String WHATEVER_ID = "PERL6_WHATEVER";
    public static final TextAttributesKey WHATEVER = TextAttributesKey.createTextAttributesKey(
            WHATEVER_ID,
            DefaultLanguageHighlighterColors.CLASS_NAME
    );

    /* String Literal Escapes
     * **********************
     * These map to the default string literal and bad string literal
     * colors.
     */

    static final String REGEX_BACKSLASH_BAD_ID = "PERL6_REGEX_BACKSLASH_BAD";
    public static final TextAttributesKey REGEX_BACKSLASH_BAD = TextAttributesKey.createTextAttributesKey(
            REGEX_BACKSLASH_BAD_ID,
            DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE
    );

    static final String REGEX_BUILTIN_CCLASS_ID = "PERL6_REGEX_BUILTIN_CCLASS";
    public static final TextAttributesKey REGEX_BUILTIN_CCLASS = TextAttributesKey.createTextAttributesKey(
            REGEX_BUILTIN_CCLASS_ID,
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    );

    static final String REGEX_SIG_SPACE_ID = "PERL6_REGEX_SIG_SPACE";
    public static final TextAttributesKey REGEX_SIG_SPACE = TextAttributesKey.createTextAttributesKey(
            REGEX_SIG_SPACE_ID,
            DefaultLanguageHighlighterColors.FUNCTION_CALL
    );

    static final String STRING_LITERAL_BAD_ESCAPE_ID = "PERL6_STRING_LITERAL_BAD_ESCAPE";
    public static final TextAttributesKey STRING_LITERAL_BAD_ESCAPE = TextAttributesKey.createTextAttributesKey(
            STRING_LITERAL_BAD_ESCAPE_ID,
            DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE
    );

    static final String STRING_LITERAL_ESCAPE_ID = "PERL6_STRING_LITERAL_ESCAPE";
    public static final TextAttributesKey STRING_LITERAL_ESCAPE = TextAttributesKey.createTextAttributesKey(
            STRING_LITERAL_ESCAPE_ID,
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    );

    static final String TRANS_BAD_ID = "PERL6_TRANS_BAD";
    public static final TextAttributesKey TRANS_BAD = TextAttributesKey.createTextAttributesKey(
            TRANS_BAD_ID,
            DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE
    );

    static final String TRANS_ESCAPE_ID = "PERL6_TRANS_ESCAPE";
    public static final TextAttributesKey TRANS_ESCAPE = TextAttributesKey.createTextAttributesKey(
            TRANS_ESCAPE_ID,
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    );

    /* Pod
     * ***
     * Map to the default documentation colors.
     */

    static final String POD_DIRECTIVE_ID = "PERL6_DIRECTIVE";
    public static final TextAttributesKey POD_DIRECTIVE = TextAttributesKey.createTextAttributesKey(
            POD_DIRECTIVE_ID,
            DefaultLanguageHighlighterColors.DOC_COMMENT_TAG
    );

    static final String POD_TYPENAME_ID = "PERL6_TYPENAME";
    public static final TextAttributesKey POD_TYPENAME = TextAttributesKey.createTextAttributesKey(
            POD_TYPENAME_ID,
            DefaultLanguageHighlighterColors.DOC_COMMENT_TAG
    );

    static final String POD_CONFIGURATION_ID = "PERL6_CONFIGURATION";
    public static final TextAttributesKey POD_CONFIGURATION = TextAttributesKey.createTextAttributesKey(
            POD_CONFIGURATION_ID,
            DefaultLanguageHighlighterColors.DOC_COMMENT_TAG
    );

    static final String POD_TEXT_ID = "PERL6_TEXT";
    public static final TextAttributesKey POD_TEXT = TextAttributesKey.createTextAttributesKey(
            POD_TEXT_ID,
            DefaultLanguageHighlighterColors.DOC_COMMENT
    );

    static final String POD_CODE_ID = "PERL6_CODE";
    public static final TextAttributesKey POD_CODE = TextAttributesKey.createTextAttributesKey(
            POD_CODE_ID,
            DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP
    );

    static final String POD_FORMAT_CODE_ID = "PERL6_FORMAT_CODE";
    public static final TextAttributesKey POD_FORMAT_CODE = TextAttributesKey.createTextAttributesKey(
        POD_FORMAT_CODE_ID,
        DefaultLanguageHighlighterColors.DOC_COMMENT_TAG
    );

    static final String POD_FORMAT_QUOTES_ID = "PERL6_FORMAT_QUOTES";
    public static final TextAttributesKey POD_FORMAT_QUOTES = TextAttributesKey.createTextAttributesKey(
        POD_FORMAT_QUOTES_ID,
        DefaultLanguageHighlighterColors.DOC_COMMENT_TAG
    );

    static final String POD_TEXT_BOLD_ID = "PERL6_TEXT_BOLD";
    public static final TextAttributesKey POD_TEXT_BOLD = TextAttributesKey.createTextAttributesKey(
        POD_TEXT_BOLD_ID
    );

    static final String POD_TEXT_ITALIC_ID = "PERL6_TEXT_ITALIC";
    public static final TextAttributesKey POD_TEXT_ITALIC = TextAttributesKey.createTextAttributesKey(
        POD_TEXT_ITALIC_ID
    );

    static final String POD_TEXT_UNDERLINE_ID = "PERL6_TEXT_UNDERLINE";
    public static final TextAttributesKey POD_TEXT_UNDERLINE = TextAttributesKey.createTextAttributesKey(
        POD_TEXT_UNDERLINE_ID
    );

    /* Unused
     * ******
     * Map to the default styling for unused elements.
     */

    private static final String UNUSED_ID = "PERL6_UNUSED";
    public static final TextAttributesKey UNUSED = TextAttributesKey.createTextAttributesKey(
        UNUSED_ID,
        CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES
    );
}
