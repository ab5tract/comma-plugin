package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.Arrays;
import java.util.List;

public class AttributesTest extends LightCodeInsightFixtureTestCase {
    public void testOwnAttributes() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class C { has $!abc; method a() { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!", "$!abc")));
        assertEquals(2, vars.size());
    }

    public void testRoleAttributes() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Foo { has $!foo; has $.bar; }; class A does Foo { has $!a; method a() { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!", "$!a", "$!foo", "$!bar")));
        assertEquals(4, vars.size());
    }

    public void testNestedRoleAttributes() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Nested { has $!nested; }; role Foo does Nested { has $!foo; has $.bar; }; class A does Foo { has $!a; method a() { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        System.out.println(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!", "$!a", "$!foo", "$!bar", "$!nested")));
        assertEquals(5, vars.size());
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
}
