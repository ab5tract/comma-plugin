package edument.rakuidea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class IncompleteHyperPostfixThenInfixTest extends ParsingTestCase {
    public IncompleteHyperPostfixThenInfixTest() {
        super("", "p6", new RakuParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/parsing/incomplete-hyper-postfix-then-infix";
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
