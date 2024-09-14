package edument.rakuidea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class CallishVarHashIndexedTest extends ParsingTestCase {
    public CallishVarHashIndexedTest() {
        super("", "p6", new RakuParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/parsing/callish-var-hash-indexed";
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
