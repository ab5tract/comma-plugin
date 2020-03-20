package edument.perl6idea.parsing;

public class Perl6HighlighterLexer extends Perl6Lexer {
    @Override
    public void advance() {
        // Skip over zero-width tokens, which break various lexer-based features,
        // such as brace matching in some cases.
        super.advance();
        while (getTokenType() != null && getTokenStart() == getTokenEnd())
            super.advance();
    }
}
