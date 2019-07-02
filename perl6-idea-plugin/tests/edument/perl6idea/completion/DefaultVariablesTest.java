package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
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
        doFileTest("DefaultTestData1.pm6",
                   Arrays.asList("$_", "$/", "$!", "$=pod", "$?FILE", "$?LANG", "$?LINE", "$?PACKAGE"),
                   Collections.singletonList("$=finish"), 8);
    }

    public void testCompletionResources() {
        doFileTest("DefaultTestData2.pm6",
                   Arrays.asList("%?RESOURCES", "%hash"),
                   Collections.emptyList(), 4);
    }

    public void testCompletionInClass() {
        doFileTest("DefaultTestData3.pm6",
                   Arrays.asList("$?CLASS", "$?PACKAGE"),
                   Collections.singletonList("$?ROLE"), null);
    }

    public void testCompletionInRole() {
        doFileTest("DefaultTestData4.pm6", Arrays.asList("$?CLASS", "$?PACKAGE", "$?ROLE"), Collections.emptyList(), null);
    }

    public void testCompletionInGrammar() {
        doFileTest("DefaultTestData5.pm6",
                   Arrays.asList("$?CLASS", "$?PACKAGE"),
                   Collections.singletonList("$?ROLE"), null);
    }

    public void testCompletionInBlock() {
        doTextTest("my $x = { &?<caret>",
                   Collections.singletonList("&?BLOCK"),
                   Collections.singletonList("&?ROUTINE"));
    }

    public void testCompletionInPointyBlock() {
        doTextTest("my $x = -> $y { &?<caret>",
                   Collections.singletonList("&?BLOCK"),
                   Collections.singletonList("&?ROUTINE"));
    }

    public void testCompletionInSub() {
        doTextTest("sub foo() { &?<caret>",
                   Arrays.asList("&?ROUTINE", "&?BLOCK"),
                   Collections.emptyList()
        );
    }

    public void testNamedArgsHashCompletionInMethod() {
        doTextTest("method foo() { %<caret>",
                   Collections.singletonList("%_"), Collections.emptyList());
    }

    public void testNamedArgsHashCompletionInSubmethod() {
        doTextTest("submethod foo() { %<caret>",
                   Collections.singletonList("%_"), Collections.emptyList());
    }

    public void testNamedArgsHashCompletionInSub() {
        doTextTest("sub foo() { %<caret>",
                   Collections.emptyList(),
                   Collections.singletonList("%_"));
    }

    public void testPodFinishCompletion() {
        doTextTest("say $=<caret>\n\n=for finish\n\n",
                   Arrays.asList("$=pod", "$=finish"),
                   Collections.emptyList());
    }

    public void testPodFinishInBlockCompletion() {

        doTextTest("if True {\nsay $=<caret>\n}\n\n=for finish\n\n",
                   Arrays.asList("$=pod", "$=finish"),
                   Collections.emptyList());
    }

    private void doTextTest(String text, @Nullable List<String> assertTrue, @Nullable List<String> assertFalse) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, text);
        doTest(assertTrue, assertFalse, null);
    }


    private void doFileTest(String filename, @Nullable List<String> assertTrue, @Nullable List<String> assertFalse, @Nullable Integer size) {
        myFixture.configureByFile(filename);
        doTest(assertTrue, assertFalse, size);
    }

    private void doTest(@Nullable List<String> assertTrue, @Nullable List<String> assertFalse, @Nullable Integer size) {
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll((assertTrue)));
        for (String falsePositive : assertFalse) {
            assertFalse(vars.contains(falsePositive));
        }
        if (size != null)
            assertEquals(size.intValue(), vars.size());
    }
}
