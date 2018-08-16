package edument.perl6idea.parsing;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.psi.tree.TokenSet;

public class Perl6WordsScanner extends DefaultWordsScanner {
    public Perl6WordsScanner() {
        super(new Perl6Lexer(),
              // Identifiers
              TokenSet.create(Perl6TokenTypes.NAME,
                              Perl6TokenTypes.SUB_CALL_NAME,
                              Perl6TokenTypes.METHOD_CALL_NAME),
              // Comments
              TokenSet.create(Perl6TokenTypes.COMMENT),
              // Literals, no string literal here
              TokenSet.create(
                  Perl6TokenTypes.INTEGER_LITERAL, Perl6TokenTypes.COMPLEX_LITERAL,
                  Perl6TokenTypes.NUMBER_LITERAL, Perl6TokenTypes.RAT_LITERAL));
    }
}
