package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class ContextualizerTest extends ParsingTestCase {
    public ContextualizerTest() {
        super("", "p6", new Perl6ParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/parsing/contextualizer";
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
