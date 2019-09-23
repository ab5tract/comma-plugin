package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.Arrays;
import java.util.List;

public class SubCompletionTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/completion";
    }

    public void testCompletionFromLocal() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo() {}\nfo<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, "foo");
        assertEquals(2, vars.size());
    }

    public void testCompletionFromOuter() {
        myFixture.configureByFiles("IdeaFoo/Bar8.pm6", "IdeaFoo/Baz.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNull(vars);
    }

    public void testComplectionFromOurLocal() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo() {}\nfo<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, "foo");
        assertEquals(2, vars.size());
    }

    public void testCompletionFromCORE() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "se<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("sec", "sech", "set"));
        assertEquals(17, vars.size());
    }

    public void testCompletionFromImport() throws InterruptedException {
        ensureModuleIsLoaded("Test");
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use Test;\nis-<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("is-approx", "is-deeply", "isa-ok"));
        assertEquals(4, vars.size());
    }

    public void testAnonymousSubIsSafeToComplete() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub { f<caret> }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> subs = myFixture.getLookupElementStrings();
        assertNotNull(subs);
    }
}