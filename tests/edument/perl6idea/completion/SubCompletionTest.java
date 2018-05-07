package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SubCompletionTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/codeInsight/localVariables";
    }

    public void testCompletionFromLocal() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo() {}\nfo<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Collections.singletonList("foo")));
        assertEquals(2, vars.size());
    }

    public void testCompletionFromCORE() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "se<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("sec", "sech", "set")));
        assertEquals(17, vars.size());
    }

    public void testCompletionFromImport() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use Test;\nis-<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("is-approx", "is-deeply", "isa-ok")));
        assertEquals(4, vars.size());
    }
}