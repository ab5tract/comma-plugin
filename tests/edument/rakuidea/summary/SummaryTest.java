package edument.rakuidea.summary;

import com.intellij.psi.util.PsiTreeUtil;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;
import edument.rakuidea.psi.RakuRoutineDecl;
import edument.rakuidea.psi.RakuVariableDecl;
import edument.rakuidea.structureView.RakuStructureViewElement;

public class SummaryTest extends CommaFixtureTestCase {
    private void doTestRoutine(String code, String result) {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, code);
        RakuRoutineDecl el = PsiTreeUtil.getParentOfType(myFixture.getElementAtCaret(), RakuRoutineDecl.class, false);
        assertNotNull(el);
        assertEquals(result, el.summarySignature());
    }

    private void doTestVariable(String code, String result) {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, code);
        RakuVariableDecl el = PsiTreeUtil.getParentOfType(myFixture.getElementAtCaret(), RakuVariableDecl.class, false);
        assertNotNull(el);
        assertEquals(result, new RakuStructureViewElement(el).getPresentation().getPresentableText());
    }

    public void testSingleVariable() {
        doTestVariable("has $.ab<caret>cd-abcd", "$!abcd-abcd, $.abcd-abcd");
    }

    public void testManyVariables() {
        doTestVariable("has ($.a, $.b, $.as<caret>df, $!qwer)", "$!a, $!b, $!asdf, $.a, $.b, $.asdf, $!qwer");
    }

    public void testSingleSigil() {
        doTestRoutine("sub f<caret>oo($a) {}", "($)");
    }

    public void testMultiplySigils() {
        doTestRoutine("sub f<caret>oo($a, @b, %c, &d) {}", "($, @, %, &)");
    }

    public void testTypedVariable() {
        doTestRoutine("sub f<caret>oo(Int $a) {}", "(Int $)");
    }

    public void testTypedVariable2() {
        doTestRoutine("sub f<caret>oo(Int $a, Backtrace $foo) {}", "(Int $, Backtrace $)");
    }

    public void testSlurpy() {
        doTestRoutine("sub f<caret>oo($a, %b, *@c) {}", "($, %, *@)");
    }

    public void testOptional() {
        doTestRoutine("sub f<caret>oo($a, $y?) {}", "($, $?)");
    }

    public void testNameds() {
        doTestRoutine("sub f<caret>oo($a, :$foo) {}", "($, :$foo)");
        doTestRoutine("sub f<caret>oo(:$a!, Int :$b) {}", "(:$a!, Int :$b)");
    }

    public void testNamedsAlias() {
        doTestRoutine("sub f<caret>oo(:a($b)) {}", "(:$a)");
        doTestRoutine("sub f<caret>oo(:a(:$b)) {}", "(:a(:$b))");
    }

    public void testCaptureArgs() {
        doTestRoutine("sub f<caret>oo(|c) {}", "(|)");
    }

    public void testTermArgs() {
        doTestRoutine("sub f<caret>oo(\\a) {}", "(\\a)");
    }

    public void testSubSignatures() {
        doTestRoutine("sub f<caret>oo([$head, *@tail]) {}", "(@)");
        doTestRoutine("sub f<caret>oo((:$x, :$y)) {}", "($)");
    }

    public void testReturn() {
        doTestRoutine("sub f<caret>oo(Int $x --> Int) {}", "(Int $ --> Int)");
        doTestRoutine("sub f<caret>oo(Int $x) returns Int {}", "(Int $ --> Int)");
        doTestRoutine("sub f<caret>oo(Int $x) of Int {}", "(Int $ --> Int)");
    }

    public void testInvalidCases() {
        doTestRoutine("sub f<caret>oo(-- > Int) {}", "()");
        doTestRoutine("sub f<caret>oo(-->) {}", "()");
        doTestRoutine("sub f<caret>oo(Int $a) return A {}", "(Int $)");
        doTestRoutine("sub f<caret>oo {}", "()");
    }
}
