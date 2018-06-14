package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class CallishVarHashIndexed extends ParsingTestCase {
    public CallishVarHashIndexed() {
        super("", "p6", new Perl6ParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/parsing/callish-var-hash-indexed";
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
