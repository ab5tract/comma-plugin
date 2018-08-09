package edument.perl6idea.parsing;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.psi.tree.TokenSet;

public class Perl6WordsScanner extends DefaultWordsScanner {
    public Perl6WordsScanner() {
        super(new Perl6Lexer(), TokenSet.create(Perl6TokenTypes.NAME),
              TokenSet.EMPTY, TokenSet.create(Perl6TokenTypes.INTEGER_LITERAL));
    }
}
