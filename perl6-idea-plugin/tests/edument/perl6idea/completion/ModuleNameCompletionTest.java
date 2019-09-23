package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.List;

public class ModuleNameCompletionTest extends CommaFixtureTestCase {
    public void testPragmaCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use exp<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> names = myFixture.getLookupElementStrings();
        assertNull(names);
    }

    public void testVersionCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use v6<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> names = myFixture.getLookupElementStrings();
        assertEmpty(names);
    }

    public void testLibraryCompletion1() {
        doTest("Tes", "Test");
    }

    public void testLibraryCompletion2() {
        doTest("Nati", "NativeCall");
    }

    private void doTest(String prefix, String full) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, String.format("use %s<caret>", prefix));
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> names = myFixture.getLookupElementStrings();
        assertNotNull(names);
        assertTrue(names.contains(full));
    }
}
