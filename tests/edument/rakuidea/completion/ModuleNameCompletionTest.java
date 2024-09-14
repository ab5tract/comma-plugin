package edument.rakuidea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;

import java.util.List;

public class ModuleNameCompletionTest extends CommaFixtureTestCase {
    public void testPragmaCompletion() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "use exp<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> names = myFixture.getLookupElementStrings();
        assertNull(names);
    }

    public void testVersionCompletion() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "use v6<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> names = myFixture.getLookupElementStrings();
        assertEmpty(names);
    }

    public void testLibraryCompletion1() {
        doTest("Te", "Test");
    }

    public void testLibraryCompletion2() {
        doTest("Nati", "NativeCall");
    }

    private void doTest(String prefix, String full) {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, String.format("use %s<caret>", prefix));
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> names = myFixture.getLookupElementStrings();
        assertNotNull(names);
        assertContainsElements(names, full);
    }
}
