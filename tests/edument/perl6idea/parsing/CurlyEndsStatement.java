package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class CurlyEndsStatement extends ParsingTestCase {
    public CurlyEndsStatement() {
        super("", "p6", new Perl6ParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/parsing/curly-ends-statement";
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
