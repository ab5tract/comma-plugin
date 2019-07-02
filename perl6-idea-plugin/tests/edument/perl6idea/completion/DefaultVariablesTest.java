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
                   Collections.singletonList("$=finish"));
    }

    public void testCompletionResources() {
        doFileTest("DefaultTestData2.pm6",
                   Arrays.asList("%?RESOURCES", "%hash"),
                   Collections.emptyList());
    }

    public void testCompletionInClass() {
        doFileTest("DefaultTestData3.pm6",
                   Arrays.asList("$?CLASS", "$?PACKAGE"),
                   Collections.singletonList("$?ROLE"));
    }

    public void testCompletionInRole() {
        doFileTest("DefaultTestData4.pm6", Arrays.asList("$?CLASS", "$?PACKAGE", "$?ROLE"), Collections.emptyList());
    }

    public void testCompletionInGrammar() {
        doFileTest("DefaultTestData5.pm6",
                   Arrays.asList("$?CLASS", "$?PACKAGE"),
                   Collections.singletonList("$?ROLE"));
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

    public void testMAINDynamic() {
        doTextTest("sub MAIN { say $*<caret> }",
                   Arrays.asList("$*USAGE", "$*THREAD"),
                   Collections.emptyList());
    }

    public void testGenerateUsageDynamic() {
        doTextTest("sub GENERATE-USAGE { say &*<caret> }",
                   Collections.singletonList("&*GENERATE-USAGE"),
                   Collections.singletonList("&*ARGS-TO-CAPTURE"));
    }

    public void testArgsToCaptureDynamic() {
        doTextTest("sub ARGS-TO-CAPTURE { say &*<caret> }",
                   Collections.singletonList("&*ARGS-TO-CAPTURE"),
                   Collections.singletonList("&*GENERATE-USAGE"));
    }

    public void testDynamicVariables() {
        doTextTest("$*<caret>",
                   Arrays.asList("$*THREAD", "$*USER", "$*TZ", "$*COLLATION"),
                   Collections.singletonList("$*USAGE"));
    }

    private void doTextTest(String text, @Nullable List<String> assertTrue, @Nullable List<String> assertFalse) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, text);
        doTest(assertTrue, assertFalse);
    }


    private void doFileTest(String filename, @Nullable List<String> assertTrue, @Nullable List<String> assertFalse) {
        myFixture.configureByFile(filename);
        doTest(assertTrue, assertFalse);
    }

    private void doTest(@Nullable List<String> assertTrue, @Nullable List<String> assertFalse) {
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll((assertTrue)));
        for (String falsePositive : assertFalse)
            assertFalse(vars.contains(falsePositive));
    }
}
