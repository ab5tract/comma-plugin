package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.LightFixtureCompletionTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.Arrays;
import java.util.List;

public class EnumTest extends LightFixtureCompletionTestCase {
    public void testEnumCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "enum Phos <Foo1 Foo2>; my Fo<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> types = myFixture.getLookupElementStrings();
        assertNotNull(types);
        assertTrue(types.containsAll(Arrays.asList("Foo1", "Foo2")));
    }
}
