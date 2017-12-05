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
        ATTRIBUTES.put(Perl6TokenTypes.STATEMENT_CONTROL, Perl6Highlighter.STATEMENT_CONTROL);
        ATTRIBUTES.put(Perl6TokenTypes.NAME, Perl6Highlighter.TYPE_NAME);
        ATTRIBUTES.put(Perl6TokenTypes.STATEMENT_TERMINATOR, Perl6Highlighter.STATEMENT_TERMINATOR);
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
