package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.Arrays;
import java.util.List;

public class TypeCompletionTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/completion";
    }

    public void testTypesFromSetting() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my In<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("Instant", "Int"));
    }

    public void testMultipartTypesFromSetting() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my IO::<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("IO::Path", "IO::Handle"));
    }

    public void testSanityNoNativeCallWithoutImport() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say NativeCal<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertEmpty(vars);
    }

    public void testUseGlobalSymbol() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use NativeCall; say NativeCal<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("NativeCall::CStr", "NativeCall::Compiler::GNU"));
    }

    public void testNeedGlobalSymbol() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "need NativeCall; say NativeCal<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("NativeCall::CStr", "NativeCall::Compiler::GNU"));
    }

    public void testUseFindsExportedSymbol() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use NativeCall; my lon<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("long", "longlong"));
    }

    public void testNeedDoesNotFindExportedSymbol() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "need NativeCall; my lon<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertEmpty(vars);
    }

    public void testSimpleDeclaredTypeOur() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Interesting { }\nmy In<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, "Interesting");
    }

    public void testSimpleDeclaredTypeMy() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my class Interesting { }\nmy In<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, "Interesting");
    }

    public void testNestedTypesOutside() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
            "class Interesting { class Nested { class Deeper { } }; my class Lexical { } }\nmy Inter<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("Interesting", "Interesting::Nested", "Interesting::Nested::Deeper"));
        assertDoesntContain(vars, "Lexical", "INterested::Lexical");
    }

    public void testNestedTypesInside() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
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
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my class { -<caret> }");
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
