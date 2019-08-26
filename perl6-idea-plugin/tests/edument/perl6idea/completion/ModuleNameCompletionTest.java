package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.util.registry.RegistryValue;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ModuleNameCompletionTest extends LightCodeInsightFixtureTestCase {
    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    public void testPragmaCompletion() {
        doTest("exp", "experimental");
    }

    public void testVersionCompletion() {
        doTest("v6", "v6.c");
    }

    public void testLibraryCompletion1() {
        doTest("Te", "Test");
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
