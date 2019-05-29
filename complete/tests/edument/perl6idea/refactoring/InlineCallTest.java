package edument.perl6idea.refactoring;

import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import edument.perl6idea.refactoring.inline.Perl6InlineCallActionHandler;

public class InlineCallTest extends LightPlatformCodeInsightFixtureTestCase {
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
            "Cannot perform inline refactoring: lexical variables are used in original routine that are not available at call location", this::doTest);
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
            "Cannot perform inline refactoring: attributes of class that contains a routine are used", this::doTest);
    }

    private void doTest() {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        Perl6InlineCallActionHandler action = new Perl6InlineCallActionHandler();
        action.inlineElement(getProject(), myFixture.getEditor(), myFixture.getElementAtCaret());
        myFixture.checkResultByFile(getTestName(true) + ".p6");
    }
}
