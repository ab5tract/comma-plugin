package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class MetaOpsTest extends ParsingTestCase {
    public MetaOpsTest() {
        super("", "p6", new Perl6ParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/parsing/metaops";
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
