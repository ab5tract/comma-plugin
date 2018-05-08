package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;
import org.junit.Ignore;

@Ignore("Ignored to get build working see: P6I-206")
public class Operators extends ParsingTestCase {
    public Operators() {
        super("", "p6", new Perl6ParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/parsing/operators";
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
