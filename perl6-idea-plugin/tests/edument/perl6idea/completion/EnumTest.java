package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.LightFixtureCompletionTestCase;
import com.intellij.testFramework.LightProjectDescriptor;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class EnumTest extends LightFixtureCompletionTestCase {
    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    public void testEnumCompletionStringLiteral() {
        doTest("enum Phospho <FooName1 FooName2>; my FooName<caret>",
               Arrays.asList("FooName1", "FooName2", "Phospho::FooName1", "Phospho::FooName2"), 4);
    }

    public void testEnumCompletionNamedValues() {
        doTest("enum Phospho ( FooName1 => 1, FooName2 => 2 ); my Phosph<caret>",
               Arrays.asList("Phospho::FooName1", "Phospho::FooName2", "Phospho"), 3);
    }

    public void testEnumFullReference() {
        doTest("enum Phospho <\nFoo1\nFoo2\n>; my Phosph<caret>", Arrays.asList("Phospho", "Phospho::Foo1", "Phospho::Foo2"), 3);
    }

    public void doTest(String text, List<String> values, int len) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, text);
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> types = myFixture.getLookupElementStrings();
        assertNotNull(types);
        assertEquals(len, types.size());
        assertTrue(types.containsAll(values));
    }
}
