package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.Arrays;
import java.util.List;

public class ClassRoleCompletionTest extends LightCodeInsightFixtureTestCase {
    public void testSimpleRole() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Foo { has $!foo; method test { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!foo", "$!")));
    }

    public void testCompositionInternals() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Foo { has $!foo; has $!bar; }; class A does Foo { method test { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!foo", "$!bar")));
    }

    public void testInheritanceInternals() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class Foo { has $!foo; has $!bar; }; class A is Foo { method test { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNull(vars);
    }

    public void testRoleIntoRoleComposiitonInternals() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Foo { has $!foo; has $!bar; }; role A does Foo { method test { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNull(vars);
    }

    public void testClassIntoClassInheritanceInternals() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class Foo { has $!foo; has $!bar; }; class A is Foo { method test { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNull(vars);
    }

    public void testRoleInMiddleDoesNotHaveAttrs1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role A { has $!foo }; role B does A { method test { say $!<caret> } }; class C does B {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNull(vars);
    }

    public void testRoleInMiddleDoesNotHaveAttrs2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role A { has $!foo }; role B does A {}; class C does B { method test { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!foo", "$!")));
    }

    public void testRoleInMiddleHasMethods1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role A { method !a {} }; role B does A { method test { self!<caret> } }; class C does B {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNull(vars);
    }

    public void testRoleInMiddleHasMethods2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role A { method !a {} }; role B does A {}; class C does B { method test { self!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNull(vars);
    }
}
