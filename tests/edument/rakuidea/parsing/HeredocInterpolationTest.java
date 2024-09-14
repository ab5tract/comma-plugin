package edument.rakuidea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class HeredocInterpolationTest extends ParsingTestCase {
    public HeredocInterpolationTest() {
        super("", "p6", new RakuParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/parsing/heredoc-interpolation";
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
