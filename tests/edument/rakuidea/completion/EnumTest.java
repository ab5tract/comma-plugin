package edument.rakuidea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EnumTest extends CommaFixtureTestCase {
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

    public void testExternalEnum() {
        doTest("SeekTy<caret>", Arrays.asList("SeekType", "SeekType::SeekFromBeginning", "SeekType::SeekFromCurrent", "SeekType::SeekFromEnd"), 4);
    }

    public void testExternalSubset() {
        doTest("UIn<caret>", Collections.singletonList("UInt"), 5);
    }

    public void doTest(String text, List<String> values, int len) {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, text);
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> types = myFixture.getLookupElementStrings();
        assertNotNull(types);
        assertContainsElements(types, values);
        assertEquals(len, types.size());
    }
}
