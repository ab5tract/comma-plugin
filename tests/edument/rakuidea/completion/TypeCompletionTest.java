package edument.rakuidea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;

import java.util.Arrays;
import java.util.List;

public class TypeCompletionTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/completion";
    }

    public void testTypesFromSetting() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "my In<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("Instant", "Int"));
    }

    public void testMultipartTypesFromSetting() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "my IO::<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("IO::Path", "IO::Handle"));
    }

    public void testSanityNoNativeCallWithoutImport() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "say NativeCal<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertEmpty(vars);
    }

    public void testUseGlobalSymbol() throws InterruptedException {
        ensureModuleIsLoaded("NativeCall");
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "use NativeCall; say NativeCal<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("NativeCall::CStr", "NativeCall::Compiler::GNU"));
    }

    public void testNeedGlobalSymbol() throws InterruptedException {
        ensureModuleIsLoaded("NativeCall", "need");
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "need NativeCall; say NativeCal<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("NativeCall::CStr", "NativeCall::Compiler::GNU"));
    }

    public void testUseFindsExportedSymbol() throws InterruptedException {
        ensureModuleIsLoaded("NativeCall");
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "use NativeCall; my lon<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("long", "longlong"));
    }

    public void testNeedDoesNotFindExportedSymbol() throws InterruptedException {
        ensureModuleIsLoaded("NativeCall", "need");
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "need NativeCall; my lon<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertEmpty(vars);
    }

    public void testSimpleDeclaredTypeOur() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "class Interesting { }\nmy In<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, "Interesting");
    }

    public void testSimpleDeclaredTypeMy() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "my class Interesting { }\nmy In<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, "Interesting");
    }

    public void testNestedTypesOutside() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE,
                                  "class Interesting { class Nested { class Deeper { } }; my class Lexical { } }\nmy Inter<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("Interesting", "Interesting::Nested", "Interesting::Nested::Deeper"));
        assertDoesntContain(vars, "Lexical", "INterested::Lexical");
    }

    public void testNestedTypesInside() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE,
                                  "class Interesting { class InterNested { class InterDeeper { } }; my class InterLexical { }; my Inter<caret> }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("Interesting", "Interesting::InterNested",
                                                   "Interesting::InterNested::InterDeeper",
                                                   "InterNested", "InterNested::InterDeeper", "InterLexical"));
        assertDoesntContain(vars, "Interested::InterLexical");
    }

    public void testAnonymousClassIsSafeToComplete() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "my class { -<caret> }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> types = myFixture.getLookupElementStrings();
        assertNotNull(types);
    }

    public void testEnumsAreExportedByDefault() {
        myFixture.configureByFiles("IdeaFoo/Bar9.pm6", "IdeaFoo/Baz.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertContainsElements(vars, Arrays.asList("ENUM::ONE", "ENUM::TWO"));
    }
}
