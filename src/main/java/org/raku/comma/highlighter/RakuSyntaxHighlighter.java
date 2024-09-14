package org.raku.comma.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.raku.comma.parsing.RakuHighlighterLexer;
import org.raku.comma.parsing.RakuTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RakuSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<>();

    static {
        ATTRIBUTES.put(RakuTokenTypes.BAD_CHARACTER, RakuHighlighter.BAD_CHARACTER);
        ATTRIBUTES.put(RakuTokenTypes.COMMENT, RakuHighlighter.COMMENT);
        ATTRIBUTES.put(RakuTokenTypes.COMMENT_STARTER, RakuHighlighter.COMMENT);
        ATTRIBUTES.put(RakuTokenTypes.COMMENT_QUOTE_OPEN, RakuHighlighter.COMMENT);
        ATTRIBUTES.put(RakuTokenTypes.COMMENT_QUOTE_CLOSE, RakuHighlighter.COMMENT);
        ATTRIBUTES.put(RakuTokenTypes.STATEMENT_CONTROL, RakuHighlighter.STATEMENT_CONTROL);
        ATTRIBUTES.put(RakuTokenTypes.PHASER, RakuHighlighter.PHASER);
        ATTRIBUTES.put(RakuTokenTypes.LABEL_NAME, RakuHighlighter.LABEL_NAME);
        ATTRIBUTES.put(RakuTokenTypes.LABEL_COLON, RakuHighlighter.LABEL_COLON);
        ATTRIBUTES.put(RakuTokenTypes.STATEMENT_PREFIX, RakuHighlighter.STATEMENT_PREFIX);
        ATTRIBUTES.put(RakuTokenTypes.STATEMENT_MOD_COND, RakuHighlighter.STATEMENT_MOD);
        ATTRIBUTES.put(RakuTokenTypes.STATEMENT_MOD_LOOP, RakuHighlighter.STATEMENT_MOD);
        ATTRIBUTES.put(RakuTokenTypes.SCOPE_DECLARATOR, RakuHighlighter.SCOPE_DECLARATOR);
        ATTRIBUTES.put(RakuTokenTypes.MULTI_DECLARATOR, RakuHighlighter.MULTI_DECLARATOR);
        ATTRIBUTES.put(RakuTokenTypes.PACKAGE_DECLARATOR, RakuHighlighter.PACKAGE_DECLARATOR);
        ATTRIBUTES.put(RakuTokenTypes.ALSO, RakuHighlighter.PACKAGE_DECLARATOR);
        ATTRIBUTES.put(RakuTokenTypes.NAME, RakuHighlighter.TYPE_NAME);
        ATTRIBUTES.put(RakuTokenTypes.PACKAGE_NAME, RakuHighlighter.TYPE_NAME);
        ATTRIBUTES.put(RakuTokenTypes.STATEMENT_TERMINATOR, RakuHighlighter.STATEMENT_TERMINATOR);
        ATTRIBUTES.put(RakuTokenTypes.PREFIX, RakuHighlighter.PREFIX);
        ATTRIBUTES.put(RakuTokenTypes.INFIX, RakuHighlighter.INFIX);
        ATTRIBUTES.put(RakuTokenTypes.METAOP, RakuHighlighter.METAOP);
        ATTRIBUTES.put(RakuTokenTypes.METHOD_CALL_OPERATOR, RakuHighlighter.INFIX);
        ATTRIBUTES.put(RakuTokenTypes.INVOCANT_MARKER, RakuHighlighter.INFIX);
        ATTRIBUTES.put(RakuTokenTypes.LAMBDA, RakuHighlighter.LAMBDA);
        ATTRIBUTES.put(RakuTokenTypes.POSTFIX, RakuHighlighter.POSTFIX);
        ATTRIBUTES.put(RakuTokenTypes.ARRAY_INDEX_BRACKET_OPEN, RakuHighlighter.ARRAY_INDEXER);
        ATTRIBUTES.put(RakuTokenTypes.ARRAY_INDEX_BRACKET_CLOSE, RakuHighlighter.ARRAY_INDEXER);
        ATTRIBUTES.put(RakuTokenTypes.HASH_INDEX_BRACKET_OPEN, RakuHighlighter.HASH_INDEXER);
        ATTRIBUTES.put(RakuTokenTypes.HASH_INDEX_BRACKET_CLOSE, RakuHighlighter.HASH_INDEXER);
        ATTRIBUTES.put(RakuTokenTypes.VARIABLE, RakuHighlighter.VARIABLE);
        ATTRIBUTES.put(RakuTokenTypes.CONTEXTUALIZER, RakuHighlighter.CONTEXTUALIZER);
        ATTRIBUTES.put(RakuTokenTypes.CONTEXTUALIZER_OPEN, RakuHighlighter.CONTEXTUALIZER);
        ATTRIBUTES.put(RakuTokenTypes.CONTEXTUALIZER_CLOSE, RakuHighlighter.CONTEXTUALIZER);
        ATTRIBUTES.put(RakuTokenTypes.SHAPE_DECLARATION, RakuHighlighter.SHAPE_DECLARATION);
        ATTRIBUTES.put(RakuTokenTypes.TYPE_DECLARATOR, RakuHighlighter.TYPE_DECLARATOR);
        ATTRIBUTES.put(RakuTokenTypes.TERM_DECLARATION_BACKSLASH, RakuHighlighter.TERM_DECLARATION_BACKSLASH);
        ATTRIBUTES.put(RakuTokenTypes.INTEGER_LITERAL, RakuHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(RakuTokenTypes.NUMBER_LITERAL, RakuHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(RakuTokenTypes.RAT_LITERAL, RakuHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(RakuTokenTypes.COMPLEX_LITERAL, RakuHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(RakuTokenTypes.RADIX_NUMBER, RakuHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(RakuTokenTypes.STRING_LITERAL_QUOTE_SYNTAX, RakuHighlighter.STRING_LITERAL_QUOTE);
        ATTRIBUTES.put(RakuTokenTypes.STRING_LITERAL_QUOTE_OPEN, RakuHighlighter.STRING_LITERAL_QUOTE);
        ATTRIBUTES.put(RakuTokenTypes.STRING_LITERAL_QUOTE_CLOSE, RakuHighlighter.STRING_LITERAL_QUOTE);
        ATTRIBUTES.put(RakuTokenTypes.STRING_LITERAL_CHAR, RakuHighlighter.STRING_LITERAL_CHAR);
        ATTRIBUTES.put(RakuTokenTypes.STRING_LITERAL_ESCAPE, RakuHighlighter.STRING_LITERAL_ESCAPE);
        ATTRIBUTES.put(RakuTokenTypes.STRING_LITERAL_REQUOTE_ESCAPE, RakuHighlighter.STRING_LITERAL_ESCAPE);
        ATTRIBUTES.put(RakuTokenTypes.QUOTE_REGEX, RakuHighlighter.QUOTE_REGEX);
        ATTRIBUTES.put(RakuTokenTypes.QUOTE_PAIR, RakuHighlighter.QUOTE_PAIR);
        ATTRIBUTES.put(RakuTokenTypes.QUOTE_MOD, RakuHighlighter.QUOTE_MOD);
        ATTRIBUTES.put(RakuTokenTypes.ARRAY_COMPOSER_OPEN, RakuHighlighter.ARRAY_COMPOSER);
        ATTRIBUTES.put(RakuTokenTypes.ARRAY_COMPOSER_CLOSE, RakuHighlighter.ARRAY_COMPOSER);
        ATTRIBUTES.put(RakuTokenTypes.VERSION, RakuHighlighter.VERSION);
        ATTRIBUTES.put(RakuTokenTypes.BAD_ESCAPE, RakuHighlighter.STRING_LITERAL_BAD_ESCAPE);
        ATTRIBUTES.put(RakuTokenTypes.PARENTHESES_OPEN, RakuHighlighter.PARENTHESES);
        ATTRIBUTES.put(RakuTokenTypes.PARENTHESES_CLOSE, RakuHighlighter.PARENTHESES);
        ATTRIBUTES.put(RakuTokenTypes.SIGNATURE_BRACKET_OPEN, RakuHighlighter.PARENTHESES);
        ATTRIBUTES.put(RakuTokenTypes.SIGNATURE_BRACKET_CLOSE, RakuHighlighter.PARENTHESES);
        ATTRIBUTES.put(RakuTokenTypes.SUB_CALL_NAME, RakuHighlighter.SUB_CALL_NAME);
        ATTRIBUTES.put(RakuTokenTypes.METHOD_CALL_NAME, RakuHighlighter.METHOD_CALL_NAME);
        ATTRIBUTES.put(RakuTokenTypes.TERM, RakuHighlighter.TERM);
        ATTRIBUTES.put(RakuTokenTypes.SELF, RakuHighlighter.SELF);
        ATTRIBUTES.put(RakuTokenTypes.WHATEVER, RakuHighlighter.WHATEVER);
        ATTRIBUTES.put(RakuTokenTypes.HYPER_WHATEVER, RakuHighlighter.WHATEVER);
        ATTRIBUTES.put(RakuTokenTypes.STUB_CODE, RakuHighlighter.STUB_CODE);
        ATTRIBUTES.put(RakuTokenTypes.CAPTURE_TERM, RakuHighlighter.CAPTURE_TERM);
        ATTRIBUTES.put(RakuTokenTypes.ROUTINE_DECLARATOR, RakuHighlighter.ROUTINE_DECLARATOR);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_DECLARATOR, RakuHighlighter.ROUTINE_DECLARATOR);
        ATTRIBUTES.put(RakuTokenTypes.ROUTINE_NAME, RakuHighlighter.ROUTINE_NAME);
        ATTRIBUTES.put(RakuTokenTypes.PARAMETER_SEPARATOR, RakuHighlighter.PARAMETER_SEPARATOR);
        ATTRIBUTES.put(RakuTokenTypes.NAMED_PARAMETER_SYNTAX, RakuHighlighter.NAMED_PARAMETER_SYNTAX);
        ATTRIBUTES.put(RakuTokenTypes.NAMED_PARAMETER_NAME_ALIAS, RakuHighlighter.NAMED_PARAMETER_NAME_ALIAS);
        ATTRIBUTES.put(RakuTokenTypes.PARAMETER_QUANTIFIER, RakuHighlighter.PARAMETER_QUANTIFIER);
        ATTRIBUTES.put(RakuTokenTypes.WHERE_CONSTRAINT, RakuHighlighter.WHERE_CONSTRAINT);
        ATTRIBUTES.put(RakuTokenTypes.RETURN_ARROW, RakuHighlighter.RETURN_ARROW);
        ATTRIBUTES.put(RakuTokenTypes.BLOCK_CURLY_BRACKET_OPEN, RakuHighlighter.BLOCK_CURLY_BRACKETS);
        ATTRIBUTES.put(RakuTokenTypes.BLOCK_CURLY_BRACKET_CLOSE, RakuHighlighter.BLOCK_CURLY_BRACKETS);
        ATTRIBUTES.put(RakuTokenTypes.ONLY_STAR, RakuHighlighter.ONLY_STAR);
        ATTRIBUTES.put(RakuTokenTypes.PAIR_KEY, RakuHighlighter.PAIR_KEY);
        ATTRIBUTES.put(RakuTokenTypes.COLON_PAIR, RakuHighlighter.PAIR_KEY);
        ATTRIBUTES.put(RakuTokenTypes.TRAIT, RakuHighlighter.TRAIT);
        ATTRIBUTES.put(RakuTokenTypes.TYPE_COERCION_PARENTHESES_OPEN, RakuHighlighter.TYPE_COERCION_PARENTHESES);
        ATTRIBUTES.put(RakuTokenTypes.TYPE_COERCION_PARENTHESES_CLOSE, RakuHighlighter.TYPE_COERCION_PARENTHESES);
        ATTRIBUTES.put(RakuTokenTypes.TYPE_PARAMETER_BRACKET, RakuHighlighter.TYPE_PARAMETER_BRACKET);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_INFIX, RakuHighlighter.REGEX_INFIX);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_ANCHOR, RakuHighlighter.REGEX_ANCHOR);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_GROUP_BRACKET_OPEN, RakuHighlighter.REGEX_GROUP_BRACKET);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_GROUP_BRACKET_CLOSE, RakuHighlighter.REGEX_GROUP_BRACKET);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_CAPTURE_PARENTHESES_OPEN, RakuHighlighter.REGEX_CAPTURE);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_CAPTURE_PARENTHESES_CLOSE, RakuHighlighter.REGEX_CAPTURE);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_CAPTURE_NAME, RakuHighlighter.REGEX_CAPTURE);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_QUANTIFIER, RakuHighlighter.REGEX_QUANTIFIER);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_BUILTIN_CCLASS, RakuHighlighter.REGEX_BUILTIN_CCLASS);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_BACKSLASH_BAD, RakuHighlighter.REGEX_BACKSLASH_BAD);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_ASSERTION_ANGLE_OPEN, RakuHighlighter.REGEX_ASSERTION_ANGLE);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_ASSERTION_ANGLE_CLOSE, RakuHighlighter.REGEX_ASSERTION_ANGLE);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_LOOKAROUND, RakuHighlighter.REGEX_LOOKAROUND);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_CCLASS_SYNTAX, RakuHighlighter.REGEX_CCLASS_SYNTAX);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_MOD_INTERNAL, RakuHighlighter.REGEX_MOD);
        ATTRIBUTES.put(RakuTokenTypes.REGEX_MOD_UNKNOWN, RakuHighlighter.REGEX_MOD);
        ATTRIBUTES.put(RakuTokenTypes.TRANS_CHAR, RakuHighlighter.TRANS_CHAR);
        ATTRIBUTES.put(RakuTokenTypes.TRANS_RANGE, RakuHighlighter.TRANS_RANGE);
        ATTRIBUTES.put(RakuTokenTypes.TRANS_ESCAPE, RakuHighlighter.TRANS_ESCAPE);
        ATTRIBUTES.put(RakuTokenTypes.TRANS_BAD, RakuHighlighter.TRANS_BAD);
        ATTRIBUTES.put(RakuTokenTypes.POD_DIRECTIVE, RakuHighlighter.POD_DIRECTIVE);
        ATTRIBUTES.put(RakuTokenTypes.POD_TYPENAME, RakuHighlighter.POD_TYPENAME);
        ATTRIBUTES.put(RakuTokenTypes.POD_CONFIGURATION, RakuHighlighter.POD_CONFIGURATION);
        ATTRIBUTES.put(RakuTokenTypes.POD_TEXT, RakuHighlighter.POD_TEXT);
        ATTRIBUTES.put(RakuTokenTypes.POD_CODE, RakuHighlighter.POD_CODE);
        ATTRIBUTES.put(RakuTokenTypes.FORMAT_CODE, RakuHighlighter.POD_FORMAT_CODE);
        ATTRIBUTES.put(RakuTokenTypes.POD_FORMAT_STARTER, RakuHighlighter.POD_FORMAT_QUOTES);
        ATTRIBUTES.put(RakuTokenTypes.POD_FORMAT_STOPPER, RakuHighlighter.POD_FORMAT_QUOTES);
        ATTRIBUTES.put(RakuTokenTypes.POD_FORMAT_SEPARATOR, RakuHighlighter.POD_FORMAT_QUOTES);
        ATTRIBUTES.put(RakuTokenTypes.QUASI, RakuHighlighter.QUASI);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new RakuHighlighterLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        return SyntaxHighlighterBase.pack(ATTRIBUTES.get(tokenType));
    }
}
