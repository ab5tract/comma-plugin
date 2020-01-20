package edument.perl6idea.cro.template.highlighter;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

public class CroTemplateHighlighter {
    /* Errors */

    public static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
            "CRO_TEMPLATE_BAD_CHARACTER",
            HighlighterColors.BAD_CHARACTER
    );

    /* Literal content */

    public static final TextAttributesKey LITERAL_TAG = TextAttributesKey.createTextAttributesKey(
            "CRO_TEMPLATE_LITERAL_TAG",
            DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP);

    public static final TextAttributesKey LITERAL_TAG_NAME = TextAttributesKey.createTextAttributesKey(
            "CRO_TEMPLATE_LITERAL_TAG_NAME",
            DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);

    public static final TextAttributesKey LITERAL_ATTRIBUTE_NAME = TextAttributesKey.createTextAttributesKey(
            "CRO_TEMPLATE_LITERAL_ATTRIBUTE_NAME",
            DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);

    public static final TextAttributesKey LITERAL_ATTRIBUTE_VALUE = TextAttributesKey.createTextAttributesKey(
            "CRO_TEMPLATE_LITERAL_ATTRIBUTE_VALUE",
            DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);

    public static final TextAttributesKey LITERAL_TEXT = TextAttributesKey.createTextAttributesKey(
            "CRO_TEMPLATE_LITERAL_TEXT",
            DefaultLanguageHighlighterColors.DOC_COMMENT);

    /* Template language syntax elements. */

    static final String TEMPLATE_TAG_ID = "CRO_TEMPLATE_TAG";
    public static final TextAttributesKey TEMPLATE_TAG = TextAttributesKey.createTextAttributesKey(
            TEMPLATE_TAG_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String DECLARATION_ID = "CRO_TEMPLATE_DECLARATION";
    public static final TextAttributesKey DECLARATION = TextAttributesKey.createTextAttributesKey(
            DECLARATION_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

    static final String MODULE_NAME_ID = "CRO_TEMPLATE_MODULE_NAME";
    public static final TextAttributesKey MODULE_NAME = TextAttributesKey.createTextAttributesKey(
            MODULE_NAME_ID,
            DefaultLanguageHighlighterColors.CONSTANT
    );

    static final String VARIABLE_ID = "CRO_TEMPLATE_VARIABLE";
    public static final TextAttributesKey VARIABLE = TextAttributesKey.createTextAttributesKey(
            VARIABLE_ID,
            DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    );

    static final String CALL_NAME_ID = "CRO_TEMPLATE_CALL_NAME";
    public static final TextAttributesKey CALL_NAME = TextAttributesKey.createTextAttributesKey(
            CALL_NAME_ID,
            DefaultLanguageHighlighterColors.FUNCTION_CALL
    );

    static final String INFIX_ID = "CRO_TEMPLATE_INFIX";
    public static final TextAttributesKey INFIX = TextAttributesKey.createTextAttributesKey(
            INFIX_ID,
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static final String NUMERIC_LITERAL_ID = "CRO_TEMPLATE_NUMERIC_LITERAL";
    public static final TextAttributesKey NUMERIC_LITERAL = TextAttributesKey.createTextAttributesKey(
            NUMERIC_LITERAL_ID,
            DefaultLanguageHighlighterColors.NUMBER
    );

    static final String STRING_LITERAL_QUOTE_ID = "CRO_TEMPLATE_STRING_LITERAL_QUOTE";
    public static final TextAttributesKey STRING_LITERAL_QUOTE = TextAttributesKey.createTextAttributesKey(
            STRING_LITERAL_QUOTE_ID,
            DefaultLanguageHighlighterColors.STRING
    );

    static final String STRING_LITERAL_CHAR_ID = "CRO_TEMPLATE_STRING_LITERAL_CHAR";
    public static final TextAttributesKey STRING_LITERAL_CHAR = TextAttributesKey.createTextAttributesKey(
            STRING_LITERAL_CHAR_ID,
            DefaultLanguageHighlighterColors.STRING
    );

    static final String BOOL_LITERAL_ID = "CRO_TEMPLATE_BOOL_LITERAL";
    public static final TextAttributesKey BOOL_LITERAL = TextAttributesKey.createTextAttributesKey(
            BOOL_LITERAL_ID,
            DefaultLanguageHighlighterColors.CONSTANT
    );

    static final String PARENS_ID = "CRO_TEMPLATE_PARENS";
    public static final TextAttributesKey PARENS = TextAttributesKey.createTextAttributesKey(
            PARENS_ID,
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    static final String BRACKETS_ID = "CRO_TEMPLATE_BRACKETS";
    public static final TextAttributesKey BRACKETS = TextAttributesKey.createTextAttributesKey(
            BRACKETS_ID,
            DefaultLanguageHighlighterColors.BRACKETS
    );

    static final String BRACES_ID = "CRO_TEMPLATE_BRACES";
    public static final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey(
            BRACES_ID,
            DefaultLanguageHighlighterColors.BRACES
    );

    static final String COMMA_ID = "CRO_TEMPLATE_COMMA";
    public static final TextAttributesKey COMMA = TextAttributesKey.createTextAttributesKey(
            COMMA_ID,
            DefaultLanguageHighlighterColors.COMMA
    );

    static final String NAMED_ARGUMENT_SYNTAX_ID = "CRO_TEMPLATE_NAMED_ARGUMENT_SYNTAX";
    public static final TextAttributesKey NAMED_ARGUMENT_SYNTAX = TextAttributesKey.createTextAttributesKey(
            NAMED_ARGUMENT_SYNTAX_ID,
            DefaultLanguageHighlighterColors.COMMA
    );

    static final String COMMENT_ID = "CRO_TEMPLATE_COMMENT";
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(
            COMMENT_ID,
            DefaultLanguageHighlighterColors.BLOCK_COMMENT
    );
}
