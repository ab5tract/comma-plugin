package edument.rakuidea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class ConstantWithExportTagTest extends ParsingTestCase {
    public ConstantWithExportTagTest() {
        super("", "p6", new RakuParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/parsing/constant-with-export-tag";
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
