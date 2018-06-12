package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class UnbalancedBug1 extends ParsingTestCase {
    public UnbalancedBug1() {
        super("", "p6", new Perl6ParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/parsing/unbalanced-bug-1";
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
