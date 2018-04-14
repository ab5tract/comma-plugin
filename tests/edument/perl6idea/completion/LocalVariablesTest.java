package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LocalVariablesTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/codeInsight/localVariables";
    }

    public void testCompletion() {
        myFixture.configureByFile("DefaultTestDataSigil.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Collections.singletonList("$a")));
        assertEquals(10, vars.size());

        myFixture.type("a;\n@");
        myFixture.complete(CompletionType.BASIC, 1);
        vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("@b1", "@b2")));
        assertEquals(2, vars.size());

        myFixture.type("b1;\n%");
        myFixture.complete(CompletionType.BASIC, 1);
        vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("%c1", "%c2")));
        assertEquals(3, vars.size());

        myFixture.type("c1;\n&");
        myFixture.complete(CompletionType.BASIC, 1);
        vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("&d1", "&d2")));
        assertEquals(2, vars.size());
    }
}