package edument.rakuidea.cro.parsing;

import com.intellij.testFramework.ParsingTestCase;
import edument.rakuidea.cro.template.parsing.CroTemplateParserDefinition;

public abstract class CroTemplateParsingTestCase extends ParsingTestCase {
    public CroTemplateParsingTestCase(String testPath) {
        super(testPath, "crotmp", new CroTemplateParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/cro-template-parsing";
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
