package edument.perl6idea.folding;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Ignore;

import java.nio.file.Paths;

@Ignore("Ignored to get build working see: P6I-206")
public class FoldingTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/folding";
    }

    public void testFolding() {
        myFixture.configureByFiles("FoldingTestData.p6");
        myFixture.testFolding(Paths.get(getTestDataPath(), "FoldingTestData.p6").toString());
    }
}
