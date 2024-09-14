package org.raku.comma.cro.template.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.raku.comma.cro.template.parsing.CroTemplateLexer;
import org.raku.comma.cro.template.parsing.CroTemplateTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CroTemplateSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<>();

    static {
        ATTRIBUTES.put(CroTemplateTokenTypes.BAD_CHARACTER, CroTemplateHighlighter.BAD_CHARACTER);
        ATTRIBUTES.put(CroTemplateTokenTypes.COMMENT, CroTemplateHighlighter.COMMENT);
        ATTRIBUTES.put(CroTemplateTokenTypes.HTML_COMMENT, CroTemplateHighlighter.COMMENT);

        ATTRIBUTES.put(CroTemplateTokenTypes.LITERAL_TAG_OPEN, CroTemplateHighlighter.LITERAL_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.LITERAL_TAG_CLOSE, CroTemplateHighlighter.LITERAL_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.LITERAL_TAG_SLASH, CroTemplateHighlighter.LITERAL_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.LITERAL_TAG_NAME, CroTemplateHighlighter.LITERAL_TAG_NAME);
        ATTRIBUTES.put(CroTemplateTokenTypes.ATTRIBUTE_NAME, CroTemplateHighlighter.LITERAL_ATTRIBUTE_NAME);
        ATTRIBUTES.put(CroTemplateTokenTypes.ATTRIBUTE_VALUE, CroTemplateHighlighter.LITERAL_ATTRIBUTE_VALUE);
        ATTRIBUTES.put(CroTemplateTokenTypes.ATTRIBUTE_QUOTE, CroTemplateHighlighter.LITERAL_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.ATTRIBUTE_EQUALS, CroTemplateHighlighter.LITERAL_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.LITERAL_TEXT, CroTemplateHighlighter.LITERAL_TEXT);

        ATTRIBUTES.put(CroTemplateTokenTypes.TEMPLATE_TAG_OPEN, CroTemplateHighlighter.TEMPLATE_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.TEMPLATE_TAG_CLOSE, CroTemplateHighlighter.TEMPLATE_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.TEMPLATE_TAG_SLASH, CroTemplateHighlighter.TEMPLATE_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.TEMPLATE_TAG_CALL_SIGIL, CroTemplateHighlighter.TEMPLATE_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.TEMPLATE_TAG_APPLY_SIGIL, CroTemplateHighlighter.TEMPLATE_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.TEMPLATE_TAG_COND_SIGIL, CroTemplateHighlighter.TEMPLATE_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.TEMPLATE_TAG_ELSE_SIGIL, CroTemplateHighlighter.TEMPLATE_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.TEMPLATE_TAG_DECL_SIGIL, CroTemplateHighlighter.TEMPLATE_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.TEMPLATE_TAG_ITER_SIGIL, CroTemplateHighlighter.TEMPLATE_TAG);
        ATTRIBUTES.put(CroTemplateTokenTypes.DECL_OPENER, CroTemplateHighlighter.DECLARATION);

        ATTRIBUTES.put(CroTemplateTokenTypes.VARIABLE_NAME, CroTemplateHighlighter.VARIABLE);
        ATTRIBUTES.put(CroTemplateTokenTypes.MACRO_NAME, CroTemplateHighlighter.CALL_NAME);
        ATTRIBUTES.put(CroTemplateTokenTypes.SUB_NAME, CroTemplateHighlighter.CALL_NAME);
        ATTRIBUTES.put(CroTemplateTokenTypes.PART_NAME, CroTemplateHighlighter.CALL_NAME);
        ATTRIBUTES.put(CroTemplateTokenTypes.MODULE_NAME, CroTemplateHighlighter.MODULE_NAME);
        ATTRIBUTES.put(CroTemplateTokenTypes.IDENTIFER, CroTemplateHighlighter.CALL_NAME);
        ATTRIBUTES.put(CroTemplateTokenTypes.INFIX, CroTemplateHighlighter.INFIX);
        ATTRIBUTES.put(CroTemplateTokenTypes.DOT, CroTemplateHighlighter.INFIX);
        ATTRIBUTES.put(CroTemplateTokenTypes.OPEN_PAREN, CroTemplateHighlighter.PARENS);
        ATTRIBUTES.put(CroTemplateTokenTypes.CLOSE_PAREN, CroTemplateHighlighter.PARENS);
        ATTRIBUTES.put(CroTemplateTokenTypes.OPEN_BRACKET, CroTemplateHighlighter.BRACKETS);
        ATTRIBUTES.put(CroTemplateTokenTypes.CLOSE_BRACKET, CroTemplateHighlighter.BRACKETS);
        ATTRIBUTES.put(CroTemplateTokenTypes.OPEN_CURLY, CroTemplateHighlighter.BRACES);
        ATTRIBUTES.put(CroTemplateTokenTypes.CLOSE_CURLY, CroTemplateHighlighter.BRACES);
        ATTRIBUTES.put(CroTemplateTokenTypes.COMMA, CroTemplateHighlighter.COMMA);
        ATTRIBUTES.put(CroTemplateTokenTypes.NAMED_ARGUMENT_NAME, CroTemplateHighlighter.NAMED_ARGUMENT_SYNTAX);
        ATTRIBUTES.put(CroTemplateTokenTypes.NAMED_ARGUMENT_SYNTAX, CroTemplateHighlighter.NAMED_ARGUMENT_SYNTAX);
        ATTRIBUTES.put(CroTemplateTokenTypes.NAMED_PARAMETER_SYNTAX, CroTemplateHighlighter.NAMED_ARGUMENT_SYNTAX);

        ATTRIBUTES.put(CroTemplateTokenTypes.INT_LITERAL, CroTemplateHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(CroTemplateTokenTypes.RAT_LITERAL, CroTemplateHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(CroTemplateTokenTypes.NUM_LITERAL, CroTemplateHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(CroTemplateTokenTypes.BOOL_LITERAL, CroTemplateHighlighter.BOOL_LITERAL);
        ATTRIBUTES.put(CroTemplateTokenTypes.STRING_QUOTE_SINGLE, CroTemplateHighlighter.STRING_LITERAL_QUOTE);
        ATTRIBUTES.put(CroTemplateTokenTypes.STRING_TEXT, CroTemplateHighlighter.STRING_LITERAL_CHAR);
        ATTRIBUTES.put(CroTemplateTokenTypes.LITERAL_KEY, CroTemplateHighlighter.STRING_LITERAL_CHAR);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new CroTemplateLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        return SyntaxHighlighterBase.pack(ATTRIBUTES.get(tokenType));
    }
}
