package edument.rakuidea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class PodFormattingCodesTest extends ParsingTestCase {
    public PodFormattingCodesTest() {
        super("", "p6", new RakuParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/parsing/pod-formatting-codes";
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
