package edument.perl6idea.docs;

import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import edument.perl6idea.CommaFixtureTestCase;

import java.util.List;

public class DocumentationTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/docs";
    }

    private void testGeneratedDoc(String result) {
        myFixture.configureByFile(getTestName(true) + ".p6");
        PsiElement element = myFixture.getElementAtCaret();
        DocumentationProvider provider = DocumentationManager.getProviderFromElement(element);
        String quickNavigateInfo = provider.generateDoc(element, null);
        assertEquals(result, quickNavigateInfo);
    }

    private void testQuickDoc(String result) {
        myFixture.configureByFile(getTestName(true) + ".p6");
        PsiElement element = myFixture.getElementAtCaret();
        DocumentationProvider provider = DocumentationManager.getProviderFromElement(element);
        String quickNavigateInfo = provider.getQuickNavigateInfo(element, null);
        assertEquals(result, quickNavigateInfo);
    }

    private void testURL(String result) {
        myFixture.configureByFile(getTestName(true) + ".p6");
        PsiElement element = myFixture.getElementAtCaret();
        DocumentationProvider provider = DocumentationManager.getProviderFromElement(element);
        List<String> quickNavigateInfo = provider.getUrlFor(element, null);
        assertContainsElements(quickNavigateInfo, result);
    }

    public void testQuickDocsClass() {
        testGeneratedDoc("Base class for magicians<br>Trailing one");
        testQuickDoc("class Magician is Cool does Int");
    }

    public void testQuickDocsRole() {
        testGeneratedDoc("Base role for magicians<br>Trailing one");
        testQuickDoc("role Magician");
    }

    public void testQuickDocsGrammar() {
        testGeneratedDoc("Base grammar for magicians<br>Trailing one");
        testQuickDoc("grammar Magician");
    }

    public void testQuickDocsRoutine() {
        testGeneratedDoc("Fight mechanics<br>Magicians only, no mortals.");
        testQuickDoc("sub duel(Magician $a, Magician $b)");
    }

    public void testQuickDocsAttribute() {
        testGeneratedDoc("One<br>Two<br>Three!<br>Very important variable");
        testQuickDoc("has $.foo = 42 (attribute of Foo)");
    }

    public void testQuickDocsAttributeNoInit() {
        testQuickDoc("has $.foo (attribute of Foo)");
    }

    public void testQuickDocsPrivateMethod() {
        testGeneratedDoc("Fight mechanics<br>Magicians only, no mortals.");
        testQuickDoc("method !duel(Magician $a, Magician $b)");
    }

    public void testQuickDocsMethod() {
        testGeneratedDoc("Fight mechanics<br>Magicians only, no mortals.");
        testQuickDoc("method duel(Magician $a, Magician $b)");
    }

    public void testQuickDocsLongPre() {
        testGeneratedDoc("Opening.<br>Second sentence");
        testQuickDoc("class QuickDocsLongPre");
    }

    public void testQuickDocsLongPost() {
        testGeneratedDoc("Opening.<br>Second sentence");
        testQuickDoc("class QuickDocsLongPost");
    }

    public void testQuickDocsLong() {
        testGeneratedDoc("Opening.<br>Second sentence<br>Ending.<br>Third sentence");
        testQuickDoc("class QuickDocsLongPre");
    }

    public void testQuickSubset() {
        testQuickDoc("subset Pos of Int");
    }

    public void testQuickEnum() {
        testQuickDoc("enum Nums (One, Two)");
    }

    public void testQuickParameter() {
        testGeneratedDoc("an important parameter<br>documented nicely");
        testQuickDoc(":$parameter = 42");
    }

    public void testQuickRegex() {
        testQuickDoc("regex foo { one two }");
    }

    public void testQuickLongRegex() {
        testQuickDoc("rule foo { ... }");
    }

    public void testQuickConstant() {
        testQuickDoc("constant \\zero = 0");
    }

    public void testExternalTypeFromCORE() {
        testQuickDoc("class IntStr");
        testGeneratedDoc("TITLE<br>class IntStr<br><br>SUBTITLE<br>Dual value integer and string<br><br>    class IntStr is Int is Str { }<br><br>The dual value types (often referred to as allomorphs) allow for the<br>representation of a value as both a string and a numeric type. Typically<br>they will be created for you when the context is \"stringy\" but they can be<br>determined to be numbers, such as in some quoting constructs:<br><br>    my $f = <42>; say $f.^name; # OUTPUT: «IntStr␤»<br><br>As a subclass of both Int and Str, an IntStr will be accepted where either<br>is expected. However, IntStr does not share object identity with Int- or<br>Str-only variants:<br><br>    my $int-str = <42>;<br>    my Int $int = $int-str; # OK!<br>    my Str $str = $int-str; # OK!<br>    say 42 ∈ <42  55  1>;   # False; ∈ operator cares about object identity<br><br>");
        testURL("https://docs.perl6.org/type/IntStr");
    }

    public void testMethodExternalFromCORE() {
        testQuickDoc("method Capture(*%_--> Mu)");
        testGeneratedDoc("Defined as:<br><br>    method Capture()<br><br>Throws X::Cannot::Capture.");
    }

    public void testMethodExternalFromCORERole() {
        testQuickDoc("method roots(Cool $n, *%_--> Mu)");
        testGeneratedDoc("multi method roots(Numeric:D: Int:D $n --> Positional)<br><br>Returns a list of the $n complex roots, which evaluate to the original<br>number when raised to the $nth power.");
    }

    public void testMethodExternalFromCOREClass() {
        testQuickDoc("method rindex(|c is raw--> Mu)");
        testGeneratedDoc("Defined as:<br><br>    multi sub    rindex(Str(Cool) $haystack, Str(Cool) $needle, Int(Cool) $startpos = $haystack.chars)<br>    multi method rindex(Str(Cool) $haystack: Str(Cool) $needle, Int(Cool) $startpos = $haystack.chars)<br><br>Coerces the first two arguments (including the invocant in method form) to<br>Str and $startpos to Int, and returns the last position of $needle in<br>$haystack not after $startpos. Returns an undefined value if $needle wasn't<br>found.<br><br>See the documentation in type Str for examples.");
    }
}
