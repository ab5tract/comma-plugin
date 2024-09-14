package edument.rakuidea.folding;

import edument.rakuidea.CommaFixtureTestCase;

import java.nio.file.Paths;

public class FoldingTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/folding";
    }

    public void testFolding() {
        myFixture.configureByFiles("FoldingTestData.p6");
        myFixture.testFolding(Paths.get(getTestDataPath(), "FoldingTestData.p6").toString());
    }
}
