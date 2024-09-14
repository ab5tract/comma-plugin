package edument.rakuidea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class EndingCurlyThenSemicolonTest extends ParsingTestCase {
    public EndingCurlyThenSemicolonTest() {
        super("", "p6", new RakuParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/parsing/ending-curly-then-semicolon";
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
