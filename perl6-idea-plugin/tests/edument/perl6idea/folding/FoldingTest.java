package edument.perl6idea.folding;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.nio.file.Paths;

public class FoldingTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/folding";
    }

    public void testFolding() {
        myFixture.configureByFiles("FoldingTestData.p6");
        myFixture.testFolding(Paths.get(getTestDataPath(), "FoldingTestData.p6").toString());
    }
}
