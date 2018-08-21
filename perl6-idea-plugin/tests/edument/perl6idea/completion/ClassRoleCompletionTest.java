package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.Arrays;
import java.util.List;

public class ClassRoleCompletionTest extends LightCodeInsightFixtureTestCase {
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
}
