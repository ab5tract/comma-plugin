package edument.rakuidea.refactoring;

import com.intellij.refactoring.util.CommonRefactoringUtil;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.refactoring.inline.call.RakuInlineCallActionHandler;

public class InlineCallTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "complete/testData/inline-call";
    }

    public void testSingleStatementBlockInlineSave() {
        doTest();
    }

    public void testSingleStatementBlockInlineNoSave() {
        doTest();
    }

    public void testMultiStatementBlockInlineSave() {
        doTest();
    }

    public void testMultiStatementBlockInlineNoSave() {
        doTest();
    }

    public void testSingleReturnMulticallSave() {
        doTest();
    }

    public void testSingleReturnMulticallNoSave() {
        doTest();
    }

    public void testPositionalArgumentSave() {
        doTest();
    }

    public void testPositionalArgumentNoSave() {
        doTest();
    }

    public void testPositionalArgumentsSave() {
        doTest();
    }

    public void testPositionalArgumentsNoSave() {
        doTest();
    }

    public void testSingleNamedArgumentSave() {
        doTest();
    }

    public void testSingleNamedArgumentNoSave() {
        doTest();
    }

    public void testNamedArgumentsSave() {
        doTest();
    }

    public void testNamedArgumentsNoSave() {
        doTest();
    }

    public void testNamedArgumentsReverseSave() {
        doTest();
    }

    public void testNamedArgumentsReverseNoSave() {
        doTest();
    }

    public void testAcceptIncompleteCallWithDefault() {
        doTest();
    }

    public void testExpressionInCallNoSave() {
        doTest();
    }

    public void testSimpleMethodInlineSave() {
        doTest();
    }

    public void testSimpleMethodInlineNoSave() {
        doTest();
    }

    public void testExpressionsAreParenthesised() {
        doTest();
    }

    public void testLiteralIsNotParenthesised() {
        doTest();
    }

    public void testEmptyRoutineIsReplacedWithNil() {
        doTest();
    }

    public void testCannotInlineProto() {
        assertThrows(
            CommonRefactoringUtil.RefactoringErrorHintException.class,
            "Cannot perform inline refactoring: inlining of routine with proto is not supported", this::doTest);
    }

    public void testCannotInlineMulti() {
        assertThrows(
            CommonRefactoringUtil.RefactoringErrorHintException.class,
            "Cannot perform inline refactoring: inlining of routine with multi is not supported", this::doTest);
    }

    public void testCannotInlineMultipleReturns() {
        assertThrows(
            CommonRefactoringUtil.RefactoringErrorHintException.class,
            "Cannot perform inline refactoring: return statement interrupts the execution flow", this::doTest);
    }

    public void testCannotInlineNonTrailingReturn() {
        assertThrows(
            CommonRefactoringUtil.RefactoringErrorHintException.class,
            "Cannot perform inline refactoring: return statement interrupts the execution flow", this::doTest);
    }

    public void testStateVariableFails() {
        assertThrows(
            CommonRefactoringUtil.RefactoringErrorHintException.class,
            "Cannot perform inline refactoring: state variables are present", this::doTest);
    }

    public void testCannotInlineWhenLexicalsAreUnavailable() {
        assertThrows(
            CommonRefactoringUtil.RefactoringErrorHintException.class,
            "Cannot perform inline refactoring: lexical is used in original code that are not available at inlining location", this::doTest);
    }

    public void testCannotInlineMethodFromOtherClassWithSelf() {
        assertThrows(
            CommonRefactoringUtil.RefactoringErrorHintException.class,
            "Cannot perform inline refactoring: a reference to `self` is found, but caller and callee are in different classes", this::doTest);
    }

    public void testCannotInlineMethodFromOtherClassWithPrivateMethod() {
        assertThrows(
            CommonRefactoringUtil.RefactoringErrorHintException.class,
            "Cannot perform inline refactoring: a reference to `self` is found, but caller and callee are in different classes", this::doTest);
    }

    public void testCannotInlineMethodFromOtherClassWithAttribute() {
        assertThrows(
            CommonRefactoringUtil.RefactoringErrorHintException.class,
            "Cannot perform inline refactoring: attributes of class are used that are not available at inlining location", this::doTest);
    }

    public void testCannotInlineWithNameShadowed() {
        assertThrows(
            CommonRefactoringUtil.RefactoringErrorHintException.class,
            "Cannot perform inline refactoring: element from original code is shadowed by another one at inlining location", this::doTest);
    }

    private void doTest() {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        RakuInlineCallActionHandler action = new RakuInlineCallActionHandler();
        action.inlineElement(getProject(), myFixture.getEditor(), myFixture.getElementAtCaret());
        myFixture.checkResultByFile(getTestName(true) + ".p6");
    }
}
