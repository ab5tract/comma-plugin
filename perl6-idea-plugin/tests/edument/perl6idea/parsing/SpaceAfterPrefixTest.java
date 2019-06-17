package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class SpaceAfterPrefixTest extends ParsingTestCase {
    public SpaceAfterPrefixTest() {
        super("", "p6", new Perl6ParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/parsing/space-after-prefix";
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

