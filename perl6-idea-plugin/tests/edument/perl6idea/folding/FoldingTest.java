package edument.perl6idea.folding;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

public class FoldingTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/folding";
    }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    public void testFolding() {
        myFixture.configureByFiles("FoldingTestData.p6");
        myFixture.testFolding(Paths.get(getTestDataPath(), "FoldingTestData.p6").toString());
    }
}
