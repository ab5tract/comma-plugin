package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.Arrays;
import java.util.List;

public class DefaultVariablesTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/codeInsight/defaultVariables";
    }

    public void testCompletion() {
        myFixture.configureByFile("DefaultTestData1.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$_", "$/", "$!", "$=finish",
                "$=pod", "$?FILE", "$?LANG", "$?LINE", "$?PACKAGE")));
        assertEquals(9, vars.size());
    }

    public void testCompletionResources() {
        myFixture.configureByFile("DefaultTestData2.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("%?RESOURCES", "%hash")));
        assertEquals(2, vars.size());
    }
}
