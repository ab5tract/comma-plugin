package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class DoesLexerBug extends ParsingTestCase {
    public DoesLexerBug() {
        super("", "p6", new Perl6ParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/parsing/does-lexer-bug";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}
