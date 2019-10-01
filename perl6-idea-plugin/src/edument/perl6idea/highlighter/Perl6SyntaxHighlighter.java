package edument.perl6idea.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import edument.perl6idea.parsing.Perl6Lexer;
import edument.perl6idea.parsing.Perl6TokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Perl6SyntaxHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<>();

    static {
        ATTRIBUTES.put(Perl6TokenTypes.BAD_CHARACTER, Perl6Highlighter.BAD_CHARACTER);
        ATTRIBUTES.put(Perl6TokenTypes.COMMENT, Perl6Highlighter.COMMENT);
        ATTRIBUTES.put(Perl6TokenTypes.STATEMENT_CONTROL, Perl6Highlighter.STATEMENT_CONTROL);
        ATTRIBUTES.put(Perl6TokenTypes.PHASER, Perl6Highlighter.PHASER);
        ATTRIBUTES.put(Perl6TokenTypes.LABEL_NAME, Perl6Highlighter.LABEL_NAME);
        ATTRIBUTES.put(Perl6TokenTypes.LABEL_COLON, Perl6Highlighter.LABEL_COLON);
        ATTRIBUTES.put(Perl6TokenTypes.STATEMENT_PREFIX, Perl6Highlighter.STATEMENT_PREFIX);
        ATTRIBUTES.put(Perl6TokenTypes.STATEMENT_MOD_COND, Perl6Highlighter.STATEMENT_MOD);
        ATTRIBUTES.put(Perl6TokenTypes.STATEMENT_MOD_LOOP, Perl6Highlighter.STATEMENT_MOD);
        ATTRIBUTES.put(Perl6TokenTypes.SCOPE_DECLARATOR, Perl6Highlighter.SCOPE_DECLARATOR);
        ATTRIBUTES.put(Perl6TokenTypes.MULTI_DECLARATOR, Perl6Highlighter.MULTI_DECLARATOR);
        ATTRIBUTES.put(Perl6TokenTypes.PACKAGE_DECLARATOR, Perl6Highlighter.PACKAGE_DECLARATOR);
        ATTRIBUTES.put(Perl6TokenTypes.ALSO, Perl6Highlighter.PACKAGE_DECLARATOR);
        ATTRIBUTES.put(Perl6TokenTypes.NAME, Perl6Highlighter.TYPE_NAME);
        ATTRIBUTES.put(Perl6TokenTypes.PACKAGE_NAME, Perl6Highlighter.TYPE_NAME);
        ATTRIBUTES.put(Perl6TokenTypes.STATEMENT_TERMINATOR, Perl6Highlighter.STATEMENT_TERMINATOR);
        ATTRIBUTES.put(Perl6TokenTypes.PREFIX, Perl6Highlighter.PREFIX);
        ATTRIBUTES.put(Perl6TokenTypes.INFIX, Perl6Highlighter.INFIX);
        ATTRIBUTES.put(Perl6TokenTypes.METAOP, Perl6Highlighter.METAOP);
        ATTRIBUTES.put(Perl6TokenTypes.METHOD_CALL_OPERATOR, Perl6Highlighter.INFIX);
        ATTRIBUTES.put(Perl6TokenTypes.INVOCANT_MARKER, Perl6Highlighter.INFIX);
        ATTRIBUTES.put(Perl6TokenTypes.LAMBDA, Perl6Highlighter.LAMBDA);
        ATTRIBUTES.put(Perl6TokenTypes.POSTFIX, Perl6Highlighter.POSTFIX);
        ATTRIBUTES.put(Perl6TokenTypes.ARRAY_INDEX_BRACKET_OPEN, Perl6Highlighter.ARRAY_INDEXER);
        ATTRIBUTES.put(Perl6TokenTypes.ARRAY_INDEX_BRACKET_CLOSE, Perl6Highlighter.ARRAY_INDEXER);
        ATTRIBUTES.put(Perl6TokenTypes.HASH_INDEX_BRACKET_OPEN, Perl6Highlighter.HASH_INDEXER);
        ATTRIBUTES.put(Perl6TokenTypes.HASH_INDEX_BRACKET_CLOSE, Perl6Highlighter.HASH_INDEXER);
        ATTRIBUTES.put(Perl6TokenTypes.VARIABLE, Perl6Highlighter.VARIABLE);
        ATTRIBUTES.put(Perl6TokenTypes.CONTEXTUALIZER, Perl6Highlighter.CONTEXTUALIZER);
        ATTRIBUTES.put(Perl6TokenTypes.SHAPE_DECLARATION, Perl6Highlighter.SHAPE_DECLARATION);
        ATTRIBUTES.put(Perl6TokenTypes.TYPE_DECLARATOR, Perl6Highlighter.TYPE_DECLARATOR);
        ATTRIBUTES.put(Perl6TokenTypes.TERM_DECLARATION_BACKSLASH, Perl6Highlighter.TERM_DECLARATION_BACKSLASH);
        ATTRIBUTES.put(Perl6TokenTypes.INTEGER_LITERAL, Perl6Highlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(Perl6TokenTypes.NUMBER_LITERAL, Perl6Highlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(Perl6TokenTypes.RAT_LITERAL, Perl6Highlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(Perl6TokenTypes.COMPLEX_LITERAL, Perl6Highlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(Perl6TokenTypes.RADIX_NUMBER, Perl6Highlighter.NUMERIC_LITERAL);
        ATTRIBUTES.put(Perl6TokenTypes.STRING_LITERAL_QUOTE_SYNTAX, Perl6Highlighter.STRING_LITERAL_QUOTE);
        ATTRIBUTES.put(Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN, Perl6Highlighter.STRING_LITERAL_QUOTE);
        ATTRIBUTES.put(Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE, Perl6Highlighter.STRING_LITERAL_QUOTE);
        ATTRIBUTES.put(Perl6TokenTypes.STRING_LITERAL_CHAR, Perl6Highlighter.STRING_LITERAL_CHAR);
        ATTRIBUTES.put(Perl6TokenTypes.STRING_LITERAL_ESCAPE, Perl6Highlighter.STRING_LITERAL_ESCAPE);
        ATTRIBUTES.put(Perl6TokenTypes.QUOTE_REGEX, Perl6Highlighter.QUOTE_REGEX);
        ATTRIBUTES.put(Perl6TokenTypes.QUOTE_PAIR, Perl6Highlighter.QUOTE_PAIR);
        ATTRIBUTES.put(Perl6TokenTypes.QUOTE_MOD, Perl6Highlighter.QUOTE_MOD);
        ATTRIBUTES.put(Perl6TokenTypes.ARRAY_COMPOSER_OPEN, Perl6Highlighter.ARRAY_COMPOSER);
        ATTRIBUTES.put(Perl6TokenTypes.ARRAY_COMPOSER_CLOSE, Perl6Highlighter.ARRAY_COMPOSER);
        ATTRIBUTES.put(Perl6TokenTypes.VERSION, Perl6Highlighter.VERSION);
        ATTRIBUTES.put(Perl6TokenTypes.BAD_ESCAPE, Perl6Highlighter.STRING_LITERAL_BAD_ESCAPE);
        ATTRIBUTES.put(Perl6TokenTypes.PARENTHESES_OPEN, Perl6Highlighter.PARENTHESES);
        ATTRIBUTES.put(Perl6TokenTypes.PARENTHESES_CLOSE, Perl6Highlighter.PARENTHESES);
        ATTRIBUTES.put(Perl6TokenTypes.SIGNATURE_BRACKET_OPEN, Perl6Highlighter.PARENTHESES);
        ATTRIBUTES.put(Perl6TokenTypes.SIGNATURE_BRACKET_CLOSE, Perl6Highlighter.PARENTHESES);
        ATTRIBUTES.put(Perl6TokenTypes.SUB_CALL_NAME, Perl6Highlighter.SUB_CALL_NAME);
        ATTRIBUTES.put(Perl6TokenTypes.METHOD_CALL_NAME, Perl6Highlighter.METHOD_CALL_NAME);
        ATTRIBUTES.put(Perl6TokenTypes.TERM, Perl6Highlighter.TERM);
        ATTRIBUTES.put(Perl6TokenTypes.SELF, Perl6Highlighter.SELF);
        ATTRIBUTES.put(Perl6TokenTypes.WHATEVER, Perl6Highlighter.WHATEVER);
        ATTRIBUTES.put(Perl6TokenTypes.HYPER_WHATEVER, Perl6Highlighter.WHATEVER);
        ATTRIBUTES.put(Perl6TokenTypes.STUB_CODE, Perl6Highlighter.STUB_CODE);
        ATTRIBUTES.put(Perl6TokenTypes.CAPTURE_TERM, Perl6Highlighter.CAPTURE_TERM);
        ATTRIBUTES.put(Perl6TokenTypes.ROUTINE_DECLARATOR, Perl6Highlighter.ROUTINE_DECLARATOR);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_DECLARATOR, Perl6Highlighter.ROUTINE_DECLARATOR);
        ATTRIBUTES.put(Perl6TokenTypes.ROUTINE_NAME, Perl6Highlighter.ROUTINE_NAME);
        ATTRIBUTES.put(Perl6TokenTypes.PARAMETER_SEPARATOR, Perl6Highlighter.PARAMETER_SEPARATOR);
        ATTRIBUTES.put(Perl6TokenTypes.NAMED_PARAMETER_SYNTAX, Perl6Highlighter.NAMED_PARAMETER_SYNTAX);
        ATTRIBUTES.put(Perl6TokenTypes.NAMED_PARAMETER_NAME_ALIAS, Perl6Highlighter.NAMED_PARAMETER_NAME_ALIAS);
        ATTRIBUTES.put(Perl6TokenTypes.PARAMETER_QUANTIFIER, Perl6Highlighter.PARAMETER_QUANTIFIER);
        ATTRIBUTES.put(Perl6TokenTypes.WHERE_CONSTRAINT, Perl6Highlighter.WHERE_CONSTRAINT);
        ATTRIBUTES.put(Perl6TokenTypes.RETURN_ARROW, Perl6Highlighter.RETURN_ARROW);
        ATTRIBUTES.put(Perl6TokenTypes.BLOCK_CURLY_BRACKET_OPEN, Perl6Highlighter.BLOCK_CURLY_BRACKETS);
        ATTRIBUTES.put(Perl6TokenTypes.BLOCK_CURLY_BRACKET_CLOSE, Perl6Highlighter.BLOCK_CURLY_BRACKETS);
        ATTRIBUTES.put(Perl6TokenTypes.ONLY_STAR, Perl6Highlighter.ONLY_STAR);
        ATTRIBUTES.put(Perl6TokenTypes.PAIR_KEY, Perl6Highlighter.PAIR_KEY);
        ATTRIBUTES.put(Perl6TokenTypes.COLON_PAIR, Perl6Highlighter.PAIR_KEY);
        ATTRIBUTES.put(Perl6TokenTypes.TRAIT, Perl6Highlighter.TRAIT);
        ATTRIBUTES.put(Perl6TokenTypes.TYPE_COERCION_PARENTHESES_OPEN, Perl6Highlighter.TYPE_COERCION_PARENTHESES);
        ATTRIBUTES.put(Perl6TokenTypes.TYPE_COERCION_PARENTHESES_CLOSE, Perl6Highlighter.TYPE_COERCION_PARENTHESES);
        ATTRIBUTES.put(Perl6TokenTypes.TYPE_PARAMETER_BRACKET, Perl6Highlighter.TYPE_PARAMETER_BRACKET);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_INFIX, Perl6Highlighter.REGEX_INFIX);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_ANCHOR, Perl6Highlighter.REGEX_ANCHOR);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_GROUP_BRACKET_OPEN, Perl6Highlighter.REGEX_GROUP_BRACKET);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_GROUP_BRACKET_CLOSE, Perl6Highlighter.REGEX_GROUP_BRACKET);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_CAPTURE_PARENTHESES_OPEN, Perl6Highlighter.REGEX_CAPTURE);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_CAPTURE_PARENTHESES_CLOSE, Perl6Highlighter.REGEX_CAPTURE);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_CAPTURE_NAME, Perl6Highlighter.REGEX_CAPTURE);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_QUANTIFIER, Perl6Highlighter.REGEX_QUANTIFIER);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_BUILTIN_CCLASS, Perl6Highlighter.REGEX_BUILTIN_CCLASS);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_BACKSLASH_BAD, Perl6Highlighter.REGEX_BACKSLASH_BAD);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_ASSERTION_ANGLE_OPEN, Perl6Highlighter.REGEX_ASSERTION_ANGLE);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_ASSERTION_ANGLE_CLOSE, Perl6Highlighter.REGEX_ASSERTION_ANGLE);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_LOOKAROUND, Perl6Highlighter.REGEX_LOOKAROUND);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_CCLASS_SYNTAX, Perl6Highlighter.REGEX_CCLASS_SYNTAX);
        ATTRIBUTES.put(Perl6TokenTypes.REGEX_MOD_INTERNAL, Perl6Highlighter.REGEX_MOD);
        ATTRIBUTES.put(Perl6TokenTypes.TRANS_CHAR, Perl6Highlighter.TRANS_CHAR);
        ATTRIBUTES.put(Perl6TokenTypes.TRANS_RANGE, Perl6Highlighter.TRANS_RANGE);
        ATTRIBUTES.put(Perl6TokenTypes.TRANS_ESCAPE, Perl6Highlighter.TRANS_ESCAPE);
        ATTRIBUTES.put(Perl6TokenTypes.TRANS_BAD, Perl6Highlighter.TRANS_BAD);
        ATTRIBUTES.put(Perl6TokenTypes.POD_DIRECTIVE, Perl6Highlighter.POD_DIRECTIVE);
        ATTRIBUTES.put(Perl6TokenTypes.POD_TYPENAME, Perl6Highlighter.POD_TYPENAME);
        ATTRIBUTES.put(Perl6TokenTypes.POD_CONFIGURATION, Perl6Highlighter.POD_CONFIGURATION);
        ATTRIBUTES.put(Perl6TokenTypes.POD_TEXT, Perl6Highlighter.POD_TEXT);
        ATTRIBUTES.put(Perl6TokenTypes.FORMAT_CODE, Perl6Highlighter.POD_FORMAT_CODE);
        ATTRIBUTES.put(Perl6TokenTypes.POD_FORMAT_STARTER, Perl6Highlighter.POD_FORMAT_QUOTES);
        ATTRIBUTES.put(Perl6TokenTypes.POD_FORMAT_STOPPER, Perl6Highlighter.POD_FORMAT_QUOTES);
        ATTRIBUTES.put(Perl6TokenTypes.QUASI, Perl6Highlighter.QUASI);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new Perl6Lexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return SyntaxHighlighterBase.pack(ATTRIBUTES.get(tokenType));
    }
}
