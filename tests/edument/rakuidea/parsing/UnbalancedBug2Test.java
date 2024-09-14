package edument.rakuidea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class UnbalancedBug2Test extends ParsingTestCase {
    public UnbalancedBug2Test() {
        super("", "p6", new RakuParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/parsing/unbalanced-bug-2";
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
