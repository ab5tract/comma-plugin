package edument.perl6idea.highlighter;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

public final class Perl6Highlighter {
    static final String BAD_CHARACTER_ID = "PERL6_BAD_CHARACTER";
    public static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
            BAD_CHARACTER_ID,
            HighlighterColors.BAD_CHARACTER
    );

    static final String COMMENT_ID = "PERL6_COMMENT";
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(
            COMMENT_ID,
            DefaultLanguageHighlighterColors.LINE_COMMENT
    );

    static final String STATEMENT_CONTROL_ID = "PERL6_STATEMENT_CONTROL";
    public static final TextAttributesKey STATEMENT_CONTROL = TextAttributesKey.createTextAttributesKey(
            STATEMENT_CONTROL_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String PHASER_ID = "PERL6_PHASER";
    public static final TextAttributesKey PHASER = TextAttributesKey.createTextAttributesKey(
            PHASER_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String STATEMENT_PREFIX_ID = "PERL6_STATEMENT_PREFIX";
    public static final TextAttributesKey STATEMENT_PREFIX = TextAttributesKey.createTextAttributesKey(
            STATEMENT_PREFIX_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String STATEMENT_MOD_ID = "PERL6_STATEMENT_MOD";
    public static final TextAttributesKey STATEMENT_MOD = TextAttributesKey.createTextAttributesKey(
            STATEMENT_MOD_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String SCOPE_DECLARATOR_ID = "PERL6_SCOPE_DECLARATOR";
    public static final TextAttributesKey SCOPE_DECLARATOR = TextAttributesKey.createTextAttributesKey(
            SCOPE_DECLARATOR_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String MULTI_DECLARATOR_ID = "PERL6_MULTI_DECLARATOR";
    public static final TextAttributesKey MULTI_DECLARATOR = TextAttributesKey.createTextAttributesKey(
            MULTI_DECLARATOR_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String ROUTINE_DECLARATOR_ID = "PERL6_ROUTINE_DECLARATOR";
    public static final TextAttributesKey ROUTINE_DECLARATOR = TextAttributesKey.createTextAttributesKey(
            ROUTINE_DECLARATOR_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String PACKAGE_DECLARATOR_ID = "PERL6_PACKAGE_DECLARATOR";
    public static final TextAttributesKey PACKAGE_DECLARATOR = TextAttributesKey.createTextAttributesKey(
            PACKAGE_DECLARATOR_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String ROUTINE_NAME_ID = "PERL6_ROUTINE_NAME";
    public static final TextAttributesKey ROUTINE_NAME = TextAttributesKey.createTextAttributesKey(
            ROUTINE_NAME_ID,
            DefaultLanguageHighlighterColors.FUNCTION_DECLARATION
    );

    static final String PARAMETER_SEPARATOR_ID = "PERL6_PARAMETER_SEPARATOR";
    public static final TextAttributesKey PARAMETER_SEPARATOR = TextAttributesKey.createTextAttributesKey(
            PARAMETER_SEPARATOR_ID,
            DefaultLanguageHighlighterColors.COMMA
    );

    static final String NAMED_PARAMETER_SYNTAX_ID = "PERL6_NAMED_PARAMETER_SYNTAX";
    public static final TextAttributesKey NAMED_PARAMETER_SYNTAX = TextAttributesKey.createTextAttributesKey(
            NAMED_PARAMETER_SYNTAX_ID,
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    static final String NAMED_PARAMETER_NAME_ALIAS_ID = "PERL6_NAMED_PARAMETER_NAME_ALIAS";
    public static final TextAttributesKey NAMED_PARAMETER_NAME_ALIAS = TextAttributesKey.createTextAttributesKey(
            NAMED_PARAMETER_NAME_ALIAS_ID,
            DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    );

    static final String WHERE_CONSTRAINT_ID = "PERL6_WHERE_CONSTRAINT";
    public static final TextAttributesKey WHERE_CONSTRAINT = TextAttributesKey.createTextAttributesKey(
            WHERE_CONSTRAINT_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String PARAMETER_QUANTIFIER_ID = "PERL6_PARAMETER_QUANTIFIER";
    public static final TextAttributesKey PARAMETER_QUANTIFIER = TextAttributesKey.createTextAttributesKey(
            PARAMETER_QUANTIFIER_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String RETURN_ARROW_ID = "PERL6_RETURN_ARROW";
    public static final TextAttributesKey RETURN_ARROW = TextAttributesKey.createTextAttributesKey(
            RETURN_ARROW_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String TYPE_NAME_ID = "PERL6_TYPE_NAME";
    public static final TextAttributesKey TYPE_NAME = TextAttributesKey.createTextAttributesKey(
            TYPE_NAME_ID,
            DefaultLanguageHighlighterColors.CLASS_REFERENCE
    );

    static final String STATEMENT_TERMINATOR_ID = "PERL6_STATEMENT_TERMINATOR";
    public static final TextAttributesKey STATEMENT_TERMINATOR = TextAttributesKey.createTextAttributesKey(
            STATEMENT_TERMINATOR_ID,
            DefaultLanguageHighlighterColors.SEMICOLON
    );

    static final String PREFIX_ID = "PERL6_PREFIX";
    public static final TextAttributesKey PREFIX = TextAttributesKey.createTextAttributesKey(
            PREFIX_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String INFIX_ID = "PERL6_INFIX";
    public static final TextAttributesKey INFIX = TextAttributesKey.createTextAttributesKey(
            INFIX_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String POSTFIX_ID = "PERL6_POSTFIX";
    public static final TextAttributesKey POSTFIX = TextAttributesKey.createTextAttributesKey(
            POSTFIX_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String METAOP_ID = "PERL6_METAOP";
    public static final TextAttributesKey METAOP = TextAttributesKey.createTextAttributesKey(
            METAOP_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String ARRAY_INDEXER_ID = "PERL6_ARRAY_INDEXER";
    public static final TextAttributesKey ARRAY_INDEXER = TextAttributesKey.createTextAttributesKey(
            ARRAY_INDEXER_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String HASH_INDEXER_ID = "PERL6_HASH_INDEXER";
    public static final TextAttributesKey HASH_INDEXER = TextAttributesKey.createTextAttributesKey(
            HASH_INDEXER_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String LAMBDA_ID = "PERL6_LAMBDA";
    public static final TextAttributesKey LAMBDA = TextAttributesKey.createTextAttributesKey(
            LAMBDA_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String VARIABLE_ID = "PERL6_VARIABLE";
    public static final TextAttributesKey VARIABLE = TextAttributesKey.createTextAttributesKey(
            VARIABLE_ID,
            DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    );

    static final String CONTEXTUALIZER_ID = "PERL6_CONTEXTUALIZER";
    public static final TextAttributesKey CONTEXTUALIZER = TextAttributesKey.createTextAttributesKey(
            CONTEXTUALIZER_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String TERM_DECLARATION_BACKSLASH_ID = "PERL6_TERM_DECLARATION_BACKSLASH";
    public static final TextAttributesKey TERM_DECLARATION_BACKSLASH = TextAttributesKey.createTextAttributesKey(
            TERM_DECLARATION_BACKSLASH_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String SHAPE_DECLARATION_ID = "PERL6_SHAPE_DECLARATION";
    public static final TextAttributesKey SHAPE_DECLARATION = TextAttributesKey.createTextAttributesKey(
            SHAPE_DECLARATION_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String TYPE_DECLARATOR_ID = "PERL6_TYPE_DECLARATOR";
    public static final TextAttributesKey TYPE_DECLARATOR = TextAttributesKey.createTextAttributesKey(
            TYPE_DECLARATOR_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String NUMERIC_LITERAL_ID = "PERL6_NUMERIC_LITERAL";
    public static final TextAttributesKey NUMERIC_LITERAL = TextAttributesKey.createTextAttributesKey(
            NUMERIC_LITERAL_ID,
            DefaultLanguageHighlighterColors.NUMBER
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

    static final String STRING_LITERAL_ESCAPE_ID = "PERL6_STRING_LITERAL_ESCAPE";
    public static final TextAttributesKey STRING_LITERAL_ESCAPE = TextAttributesKey.createTextAttributesKey(
            STRING_LITERAL_ESCAPE_ID,
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    );

    static final String STRING_LITERAL_BAD_ESCAPE_ID = "PERL6_STRING_LITERAL_BAD_ESCAPE";
    public static final TextAttributesKey STRING_LITERAL_BAD_ESCAPE = TextAttributesKey.createTextAttributesKey(
            STRING_LITERAL_BAD_ESCAPE_ID,
            DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE
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

    static final String QUOTE_MOD_ID = "PERL6_QUOTE_MOD";
    public static final TextAttributesKey QUOTE_MOD = TextAttributesKey.createTextAttributesKey(
            QUOTE_MOD_ID,
            DefaultLanguageHighlighterColors.STRING
    );

    static final String ARRAY_COMPOSER_ID = "PERL6_ARRAY_COMPOSER";
    public static final TextAttributesKey ARRAY_COMPOSER = TextAttributesKey.createTextAttributesKey(
            ARRAY_COMPOSER_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String PARENTHESES_ID = "PERL6_PARENTHESES";
    public static final TextAttributesKey PARENTHESES = TextAttributesKey.createTextAttributesKey(
            PARENTHESES_ID,
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    static final String VERSION_ID = "PERL6_VERSION";
    public static final TextAttributesKey VERSION = TextAttributesKey.createTextAttributesKey(
            VERSION_ID,
            DefaultLanguageHighlighterColors.NUMBER
    );

    static final String BLOCK_CURLY_BRACKETS_ID = "PERL6_BLOCK_CURLY_BRACKETS";
    public static final TextAttributesKey BLOCK_CURLY_BRACKETS = TextAttributesKey.createTextAttributesKey(
            BLOCK_CURLY_BRACKETS_ID,
            DefaultLanguageHighlighterColors.BRACES
    );

    static final String SUB_CALL_NAME_ID = "PERL6_SUB_CALL_NAME";
    public static final TextAttributesKey SUB_CALL_NAME = TextAttributesKey.createTextAttributesKey(
            SUB_CALL_NAME_ID,
            DefaultLanguageHighlighterColors.FUNCTION_CALL
    );

    static final String METHOD_CALL_NAME_ID = "PERL6_METHOD_CALL_NAME";
    public static final TextAttributesKey METHOD_CALL_NAME = TextAttributesKey.createTextAttributesKey(
            METHOD_CALL_NAME_ID,
            DefaultLanguageHighlighterColors.INSTANCE_METHOD
    );

    static final String TERM_ID = "PERL6_TERM";
    public static final TextAttributesKey TERM = TextAttributesKey.createTextAttributesKey(
            TERM_ID,
            DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    );

    static final String SELF_ID = "PERL6_SELF";
    public static final TextAttributesKey SELF = TextAttributesKey.createTextAttributesKey(
            SELF_ID,
            DefaultLanguageHighlighterColors.CONSTANT
    );

    static final String WHATEVER_ID = "PERL6_WHATEVER";
    public static final TextAttributesKey WHATEVER = TextAttributesKey.createTextAttributesKey(
            WHATEVER_ID,
            DefaultLanguageHighlighterColors.CONSTANT
    );

    static final String ONLY_STAR_ID = "PERL6_ONLY_STAR";
    public static final TextAttributesKey ONLY_STAR = TextAttributesKey.createTextAttributesKey(
            ONLY_STAR_ID,
            DefaultLanguageHighlighterColors.CONSTANT
    );

    static final String STUB_CODE_ID = "PERL6_STUB_CODE";
    public static final TextAttributesKey STUB_CODE = TextAttributesKey.createTextAttributesKey(
            STUB_CODE_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String CAPTURE_TERM_ID = "PERL6_CAPTURE_TERM";
    public static final TextAttributesKey CAPTURE_TERM = TextAttributesKey.createTextAttributesKey(
            CAPTURE_TERM_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String PAIR_KEY_ID = "PERL6_PAIR_KEY";
    public static final TextAttributesKey PAIR_KEY = TextAttributesKey.createTextAttributesKey(
            PAIR_KEY_ID,
            DefaultLanguageHighlighterColors.STRING
    );

    static final String TRAIT_ID = "PERL6_TRAIT";
    public static final TextAttributesKey TRAIT = TextAttributesKey.createTextAttributesKey(
            TRAIT_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String TYPE_COERCION_PARENTHESES_ID = "PERL6_TYPE_COERCION_PARENTHESES";
    public static final TextAttributesKey TYPE_COERCION_PARENTHESES = TextAttributesKey.createTextAttributesKey(
            TYPE_COERCION_PARENTHESES_ID,
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    static final String TYPE_PARAMETER_BRACKET_ID = "PERL6_TYPE_PARAMETER_BRACKET";
    public static final TextAttributesKey TYPE_PARAMETER_BRACKET = TextAttributesKey.createTextAttributesKey(
            TYPE_PARAMETER_BRACKET_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String REGEX_INFIX_ID = "PERL6_REGEX_INFIX";
    public static final TextAttributesKey REGEX_INFIX = TextAttributesKey.createTextAttributesKey(
            REGEX_INFIX_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String REGEX_ANCHOR_ID = "PERL6_REGEX_ANCHOR";
    public static final TextAttributesKey REGEX_ANCHOR = TextAttributesKey.createTextAttributesKey(
            REGEX_ANCHOR_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String REGEX_GROUP_BRACKET_ID = "PERL6_REGEX_GROUP_BRACKET";
    public static final TextAttributesKey REGEX_GROUP_BRACKET = TextAttributesKey.createTextAttributesKey(
            REGEX_GROUP_BRACKET_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String REGEX_CAPTURE_ID = "PERL6_REGEX_CAPTURE";
    public static final TextAttributesKey REGEX_CAPTURE = TextAttributesKey.createTextAttributesKey(
            REGEX_CAPTURE_ID,
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    static final String REGEX_QUANTIFIER_ID = "PERL6_REGEX_QUANTIFIER";
    public static final TextAttributesKey REGEX_QUANTIFIER = TextAttributesKey.createTextAttributesKey(
            REGEX_QUANTIFIER_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String REGEX_BUILTIN_CCLASS_ID = "PERL6_REGEX_BUILTIN_CCLASS";
    public static final TextAttributesKey REGEX_BUILTIN_CCLASS = TextAttributesKey.createTextAttributesKey(
            REGEX_BUILTIN_CCLASS_ID,
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    );

    static final String REGEX_BACKSLASH_BAD_ID = "PERL6_REGEX_BACKSLASH_BAD";
    public static final TextAttributesKey REGEX_BACKSLASH_BAD = TextAttributesKey.createTextAttributesKey(
            REGEX_BACKSLASH_BAD_ID,
            DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE
    );

    static final String REGEX_ASSERTION_ANGLE_ID = "PERL6_REGEX_ASSERTION_ANGLE";
    public static final TextAttributesKey REGEX_ASSERTION_ANGLE = TextAttributesKey.createTextAttributesKey(
            REGEX_ASSERTION_ANGLE_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String REGEX_LOOKAROUND_ID = "PERL6_REGEX_LOOKAROUND";
    public static final TextAttributesKey REGEX_LOOKAROUND = TextAttributesKey.createTextAttributesKey(
            REGEX_LOOKAROUND_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String REGEX_CCLASS_SYNTAX_ID = "PERL6_CCLASS_SYNTAX";
    public static final TextAttributesKey REGEX_CCLASS_SYNTAX = TextAttributesKey.createTextAttributesKey(
            REGEX_CCLASS_SYNTAX_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String REGEX_MOD_ID = "PERL6_REGEX_MOD";
    public static final TextAttributesKey REGEX_MOD = TextAttributesKey.createTextAttributesKey(
            REGEX_MOD_ID,
            DefaultLanguageHighlighterColors.IDENTIFIER
    );

    static final String TRANS_RANGE_ID = "PERL6_TRANS_RANGE";
    public static final TextAttributesKey TRANS_RANGE = TextAttributesKey.createTextAttributesKey(
            TRANS_RANGE_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String TRANS_CHAR_ID = "PERL6_TRANS_CHAR";
    public static final TextAttributesKey TRANS_CHAR = TextAttributesKey.createTextAttributesKey(
            TRANS_CHAR_ID,
            DefaultLanguageHighlighterColors.STRING
    );

    static final String TRANS_ESCAPE_ID = "PERL6_TRANS_ESCAPE";
    public static final TextAttributesKey TRANS_ESCAPE = TextAttributesKey.createTextAttributesKey(
            TRANS_ESCAPE_ID,
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    );

    static final String TRANS_BAD_ID = "PERL6_TRANS_BAD";
    public static final TextAttributesKey TRANS_BAD = TextAttributesKey.createTextAttributesKey(
            TRANS_BAD_ID,
            DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE
    );
}
