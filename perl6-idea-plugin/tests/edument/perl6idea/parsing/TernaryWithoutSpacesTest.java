package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class TernaryWithoutSpacesTest extends ParsingTestCase {
    public TernaryWithoutSpacesTest() {
        super("", "p6", new Perl6ParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/parsing/ternary-without-spaces";
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
