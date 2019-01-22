package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class TermDefinition extends ParsingTestCase {
    public TermDefinition() {
        super("", "p6", new Perl6ParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/parsing/term-definition";
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
