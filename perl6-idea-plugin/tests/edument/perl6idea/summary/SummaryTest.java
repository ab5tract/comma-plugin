package edument.perl6idea.summary;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.Perl6RoutineDecl;
import org.jetbrains.annotations.NotNull;

public class SummaryTest extends CommaFixtureTestCase {
    public void doTest(String code, String result) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, code);
        PsiElement el = myFixture.getElementAtCaret();
        assert el instanceof Perl6RoutineDecl;
        Perl6RoutineDecl decl = (Perl6RoutineDecl)el;
        assertEquals(result, decl.summarySignature());
    }

    public void testSingleSigil() {
        doTest("sub f<caret>oo($a) {}", "($)");
    }

    public void testMultiplySigils() {
        doTest("sub f<caret>oo($a, @b, %c, &d) {}", "($, @, %, &)");
    }

    public void testTypedVariable() {
        doTest("sub f<caret>oo(Int $a) {}", "(Int $)");
    }

    public void testTypedVariable2() {
        doTest("sub f<caret>oo(Int $a, Backtrace $foo) {}", "(Int $, Backtrace $)");
    }

    public void testSlurpy() {
        doTest("sub f<caret>oo($a, %b, *@c) {}", "($, %, *@)");
    }

    public void testOptional() {
        doTest("sub f<caret>oo($a, $y?) {}", "($, $?)");
    }

    public void testNameds() {
        doTest("sub f<caret>oo($a, :$foo) {}", "($, :$foo)");
        doTest("sub f<caret>oo(:$a!, Int :$b) {}", "(:$a!, Int :$b)");
    }

    public void testNamedsAlias() {
        doTest("sub f<caret>oo(:a($b)) {}", "(:$a)");
        doTest("sub f<caret>oo(:a(:$b)) {}", "(:a(:$b))");
    }

    public void testCaptureArgs() {
        doTest("sub f<caret>oo(|c) {}", "(|)");
    }

    public void testTermArgs() {
        doTest("sub f<caret>oo(\\a) {}", "(\\a)");
    }

    public void testSubSignatures() {
        doTest("sub f<caret>oo([$head, *@tail]) {}", "(@)");
        doTest("sub f<caret>oo((:$x, :$y)) {}", "($)");
    }

    public void testReturn() {
        doTest("sub f<caret>oo(Int $x --> Int) {}", "(Int $ --> Int)");
        doTest("sub f<caret>oo(Int $x) returns Int {}", "(Int $ --> Int)");
        doTest("sub f<caret>oo(Int $x) of Int {}", "(Int $ --> Int)");
    }

    public void testInvalidCases() {
        doTest("sub f<caret>oo(-- > Int) {}", "()");
        doTest("sub f<caret>oo(-->) {}", "()");
        doTest("sub f<caret>oo(Int $a) return A {}", "(Int $)");
        doTest("sub f<caret>oo {}", "()");
    }
}
