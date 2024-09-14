package org.raku.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.raku.parsing.RakuHighlighterLexer;
import org.raku.parsing.RakuTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RakuSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<>();

    static {
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.BAD_CHARACTER, RakuHighlighter.BAD_CHARACTER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.COMMENT, RakuHighlighter.COMMENT);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.COMMENT_STARTER, RakuHighlighter.COMMENT);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.COMMENT_QUOTE_OPEN, RakuHighlighter.COMMENT);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.COMMENT_QUOTE_CLOSE, RakuHighlighter.COMMENT);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.STATEMENT_CONTROL, RakuHighlighter.STATEMENT_CONTROL);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.PHASER, RakuHighlighter.PHASER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.LABEL_NAME, RakuHighlighter.LABEL_NAME);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.LABEL_COLON, RakuHighlighter.LABEL_COLON);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.STATEMENT_PREFIX, RakuHighlighter.STATEMENT_PREFIX);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.STATEMENT_MOD_COND, RakuHighlighter.STATEMENT_MOD);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.STATEMENT_MOD_LOOP, RakuHighlighter.STATEMENT_MOD);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.SCOPE_DECLARATOR, RakuHighlighter.SCOPE_DECLARATOR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.MULTI_DECLARATOR, RakuHighlighter.MULTI_DECLARATOR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.PACKAGE_DECLARATOR, RakuHighlighter.PACKAGE_DECLARATOR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.ALSO, RakuHighlighter.PACKAGE_DECLARATOR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.NAME, RakuHighlighter.TYPE_NAME);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.PACKAGE_NAME, RakuHighlighter.TYPE_NAME);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.STATEMENT_TERMINATOR, RakuHighlighter.STATEMENT_TERMINATOR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.PREFIX, RakuHighlighter.PREFIX);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.INFIX, RakuHighlighter.INFIX);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.METAOP, RakuHighlighter.METAOP);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.METHOD_CALL_OPERATOR, RakuHighlighter.INFIX);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.INVOCANT_MARKER, RakuHighlighter.INFIX);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.LAMBDA, RakuHighlighter.LAMBDA);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.POSTFIX, RakuHighlighter.POSTFIX);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.ARRAY_INDEX_BRACKET_OPEN, RakuHighlighter.ARRAY_INDEXER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.ARRAY_INDEX_BRACKET_CLOSE, RakuHighlighter.ARRAY_INDEXER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.HASH_INDEX_BRACKET_OPEN, RakuHighlighter.HASH_INDEXER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.HASH_INDEX_BRACKET_CLOSE, RakuHighlighter.HASH_INDEXER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.VARIABLE, RakuHighlighter.VARIABLE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.CONTEXTUALIZER, RakuHighlighter.CONTEXTUALIZER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.CONTEXTUALIZER_OPEN, RakuHighlighter.CONTEXTUALIZER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.CONTEXTUALIZER_CLOSE, RakuHighlighter.CONTEXTUALIZER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.SHAPE_DECLARATION, RakuHighlighter.SHAPE_DECLARATION);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.TYPE_DECLARATOR, RakuHighlighter.TYPE_DECLARATOR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.TERM_DECLARATION_BACKSLASH, RakuHighlighter.TERM_DECLARATION_BACKSLASH);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.INTEGER_LITERAL, RakuHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.NUMBER_LITERAL, RakuHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.RAT_LITERAL, RakuHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.COMPLEX_LITERAL, RakuHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.RADIX_NUMBER, RakuHighlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.STRING_LITERAL_QUOTE_SYNTAX, RakuHighlighter.STRING_LITERAL_QUOTE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.STRING_LITERAL_QUOTE_OPEN, RakuHighlighter.STRING_LITERAL_QUOTE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.STRING_LITERAL_QUOTE_CLOSE, RakuHighlighter.STRING_LITERAL_QUOTE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.STRING_LITERAL_CHAR, RakuHighlighter.STRING_LITERAL_CHAR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.STRING_LITERAL_ESCAPE, RakuHighlighter.STRING_LITERAL_ESCAPE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.STRING_LITERAL_REQUOTE_ESCAPE, RakuHighlighter.STRING_LITERAL_ESCAPE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.QUOTE_REGEX, RakuHighlighter.QUOTE_REGEX);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.QUOTE_PAIR, RakuHighlighter.QUOTE_PAIR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.QUOTE_MOD, RakuHighlighter.QUOTE_MOD);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.ARRAY_COMPOSER_OPEN, RakuHighlighter.ARRAY_COMPOSER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.ARRAY_COMPOSER_CLOSE, RakuHighlighter.ARRAY_COMPOSER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.VERSION, RakuHighlighter.VERSION);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.BAD_ESCAPE, RakuHighlighter.STRING_LITERAL_BAD_ESCAPE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.PARENTHESES_OPEN, RakuHighlighter.PARENTHESES);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.PARENTHESES_CLOSE, RakuHighlighter.PARENTHESES);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.SIGNATURE_BRACKET_OPEN, RakuHighlighter.PARENTHESES);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.SIGNATURE_BRACKET_CLOSE, RakuHighlighter.PARENTHESES);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.SUB_CALL_NAME, RakuHighlighter.SUB_CALL_NAME);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.METHOD_CALL_NAME, RakuHighlighter.METHOD_CALL_NAME);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.TERM, RakuHighlighter.TERM);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.SELF, RakuHighlighter.SELF);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.WHATEVER, RakuHighlighter.WHATEVER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.HYPER_WHATEVER, RakuHighlighter.WHATEVER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.STUB_CODE, RakuHighlighter.STUB_CODE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.CAPTURE_TERM, RakuHighlighter.CAPTURE_TERM);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.ROUTINE_DECLARATOR, RakuHighlighter.ROUTINE_DECLARATOR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_DECLARATOR, RakuHighlighter.ROUTINE_DECLARATOR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.ROUTINE_NAME, RakuHighlighter.ROUTINE_NAME);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.PARAMETER_SEPARATOR, RakuHighlighter.PARAMETER_SEPARATOR);
        ATTRIBUTES.put(RakuTokenTypes.NAMED_PARAMETER_SYNTAX, RakuHighlighter.NAMED_PARAMETER_SYNTAX);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.NAMED_PARAMETER_NAME_ALIAS, RakuHighlighter.NAMED_PARAMETER_NAME_ALIAS);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.PARAMETER_QUANTIFIER, RakuHighlighter.PARAMETER_QUANTIFIER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.WHERE_CONSTRAINT, RakuHighlighter.WHERE_CONSTRAINT);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.RETURN_ARROW, RakuHighlighter.RETURN_ARROW);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.BLOCK_CURLY_BRACKET_OPEN, RakuHighlighter.BLOCK_CURLY_BRACKETS);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.BLOCK_CURLY_BRACKET_CLOSE, RakuHighlighter.BLOCK_CURLY_BRACKETS);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.ONLY_STAR, RakuHighlighter.ONLY_STAR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.PAIR_KEY, RakuHighlighter.PAIR_KEY);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.COLON_PAIR, RakuHighlighter.PAIR_KEY);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.TRAIT, RakuHighlighter.TRAIT);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.TYPE_COERCION_PARENTHESES_OPEN, RakuHighlighter.TYPE_COERCION_PARENTHESES);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.TYPE_COERCION_PARENTHESES_CLOSE, RakuHighlighter.TYPE_COERCION_PARENTHESES);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.TYPE_PARAMETER_BRACKET, RakuHighlighter.TYPE_PARAMETER_BRACKET);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_INFIX, RakuHighlighter.REGEX_INFIX);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_ANCHOR, RakuHighlighter.REGEX_ANCHOR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_GROUP_BRACKET_OPEN, RakuHighlighter.REGEX_GROUP_BRACKET);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_GROUP_BRACKET_CLOSE, RakuHighlighter.REGEX_GROUP_BRACKET);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_CAPTURE_PARENTHESES_OPEN, RakuHighlighter.REGEX_CAPTURE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_CAPTURE_PARENTHESES_CLOSE, RakuHighlighter.REGEX_CAPTURE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_CAPTURE_NAME, RakuHighlighter.REGEX_CAPTURE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_QUANTIFIER, RakuHighlighter.REGEX_QUANTIFIER);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_BUILTIN_CCLASS, RakuHighlighter.REGEX_BUILTIN_CCLASS);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_BACKSLASH_BAD, RakuHighlighter.REGEX_BACKSLASH_BAD);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_ASSERTION_ANGLE_OPEN, RakuHighlighter.REGEX_ASSERTION_ANGLE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_ASSERTION_ANGLE_CLOSE, RakuHighlighter.REGEX_ASSERTION_ANGLE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_LOOKAROUND, RakuHighlighter.REGEX_LOOKAROUND);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_CCLASS_SYNTAX, RakuHighlighter.REGEX_CCLASS_SYNTAX);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_MOD_INTERNAL, RakuHighlighter.REGEX_MOD);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.REGEX_MOD_UNKNOWN, RakuHighlighter.REGEX_MOD);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.TRANS_CHAR, RakuHighlighter.TRANS_CHAR);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.TRANS_RANGE, RakuHighlighter.TRANS_RANGE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.TRANS_ESCAPE, RakuHighlighter.TRANS_ESCAPE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.TRANS_BAD, RakuHighlighter.TRANS_BAD);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.POD_DIRECTIVE, RakuHighlighter.POD_DIRECTIVE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.POD_TYPENAME, RakuHighlighter.POD_TYPENAME);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.POD_CONFIGURATION, RakuHighlighter.POD_CONFIGURATION);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.POD_TEXT, RakuHighlighter.POD_TEXT);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.POD_CODE, RakuHighlighter.POD_CODE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.FORMAT_CODE, RakuHighlighter.POD_FORMAT_CODE);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.POD_FORMAT_STARTER, RakuHighlighter.POD_FORMAT_QUOTES);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.POD_FORMAT_STOPPER, RakuHighlighter.POD_FORMAT_QUOTES);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.POD_FORMAT_SEPARATOR, RakuHighlighter.POD_FORMAT_QUOTES);
        ATTRIBUTES.put(org.raku.parsing.RakuTokenTypes.QUASI, RakuHighlighter.QUASI);
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
