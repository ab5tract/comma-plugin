package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

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
        assertEquals(4, vars.size());
    }

    public void testCompletionInClass() {
        myFixture.configureByFile("DefaultTestData3.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$?CLASS", "$?PACKAGE")));
        assertFalse(vars.contains("$?ROLE"));
    }

    public void testCompletionInRole() {
        myFixture.configureByFile("DefaultTestData4.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$?CLASS", "$?PACKAGE", "$?ROLE")));
    }

    public void testCompletionInGrammar() {
        myFixture.configureByFile("DefaultTestData5.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$?CLASS", "$?PACKAGE")));
        assertFalse(vars.contains("$?ROLE"));
    }

    public void testCompletionInBlock() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $x = { &?<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.contains("&?BLOCK"));
        assertFalse(vars.contains("&?ROUTINE"));
    }

    public void testCompletionInPointyBlock() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $x = -> $y { &?<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.contains("&?BLOCK"));
        assertFalse(vars.contains("&?ROUTINE"));
    }

    public void testCompletionInSub() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo() { &?<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("&?ROUTINE", "&?BLOCK")));
    }
}
