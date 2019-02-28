package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LocalVariablesTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/codeInsight/localVariables";
    }

    public void testCompletion() {
        myFixture.configureByFile("DefaultTestDataSigil.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Collections.singletonList("$a")));
        assertEquals(9, vars.size());

        myFixture.type("a;\n@");
        myFixture.complete(CompletionType.BASIC, 1);
        vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("@b1", "@b2")));
        assertEquals(2, vars.size());

        myFixture.type("b1;\n%cooo");
        myFixture.complete(CompletionType.BASIC, 1);
        vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("%coool1", "%coool2")));
        assertEquals(2, vars.size());

        myFixture.type("1;\n&doooooooo");
        myFixture.complete(CompletionType.BASIC, 1);
        vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("&dooooooood1", "&dooooooood2")));
        assertEquals(2, vars.size());
    }

    public void testRoutineVariablesFromSetting() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "&sec<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("&sec", "&sech")));
        assertEquals(2, vars.size());
    }

    public void testRoleParameterVariable() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role P[$name1, $name2] { method m { $na<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$name1", "$name2")));
        assertEquals(2, vars.size());
    }

    public void testAttributeCompletionWithInnerClasses() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class C { has $!abc; class Inner { has $!xyz;  method m() { say $!<caret> } } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!", "$!xyz")));
        assertEquals(2, vars.size());
    }

    public void testCommentDoesNotThrow() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "my $a = #`( comment )\n10; say $a.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertContainsElements(methods, ".abs");
    }
}