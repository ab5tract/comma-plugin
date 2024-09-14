package edument.rakuidea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class ParameterTraitAfterType extends ParsingTestCase {
    public ParameterTraitAfterType() {
        super("", "p6", new RakuParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/parsing/parameter-trait-after-type";
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
