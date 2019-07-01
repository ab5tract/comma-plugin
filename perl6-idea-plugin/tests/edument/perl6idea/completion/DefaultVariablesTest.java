package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class DefaultVariablesTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/codeInsight/defaultVariables";
    }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    public void testCompletion() {
        myFixture.configureByFile("DefaultTestData1.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(
          Arrays.asList("$_", "$/", "$!", "$=pod", "$?FILE",
                        "$?LANG", "$?LINE", "$?PACKAGE")));
        assertFalse(vars.contains("$=finish"));
        assertEquals(8, vars.size());
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

    public void testNamedArgsHashComplietionInMethod() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "method foo() { %<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.contains("%_"));
    }

    public void testNamedArgsHashComplietionInSubmethod() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "submethod foo() { %<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.contains("%_"));
    }

    public void testNamedArgsHashComplietionInSub() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo() { %<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertFalse(vars.contains("%_"));
    }

    public void testPodFinishCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say $=<caret>\n\n=for finish\n\n");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$=pod", "$=finish")));
    }

    public void testPodFinishInBlockComplection() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "if True {\nsay $=<caret>\n}\n\n=for finish\n\n");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$=pod", "$=finish")));
    }
}
