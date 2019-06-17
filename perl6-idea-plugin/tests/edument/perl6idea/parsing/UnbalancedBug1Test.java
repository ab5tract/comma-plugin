package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class UnbalancedBug1Test extends ParsingTestCase {
    public UnbalancedBug1Test() {
        super("", "p6", new Perl6ParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/parsing/unbalanced-bug-1";
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
