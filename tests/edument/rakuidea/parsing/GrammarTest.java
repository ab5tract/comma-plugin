package edument.rakuidea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class GrammarTest extends ParsingTestCase {
    public GrammarTest() {
        super("", "p6", new RakuParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/parsing/grammar";
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

