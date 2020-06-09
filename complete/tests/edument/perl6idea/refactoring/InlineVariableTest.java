package edument.perl6idea.refactoring;

import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.refactoring.inline.variable.Perl6InlineVariableActionHandler;

public class InlineVariableTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "complete/testData/inline-variable";
    }

    public void testInitSingleUsageSave() {
        doTest();
    }

    public void testInitSingleUsageNoSave() {
        doTest();
    }

    public void testInitManyUsages() {
        doTest();
    }

    public void testLateSingleInitCaseException() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class,
            "Cannot perform inline refactoring: refactoring is supported only when the initializer is present", this::doTest);
    }

    public void testIntermediateAssignmentConflict() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class,
            "Cannot perform inline refactoring: variable to be inlined has occurrences as lvalue", this::doTest);
    }

    public void testMultivarInitSingleCaseLeftSave() {
        doTest();
    }

    public void testMultivarInitSingleCaseLeftNoSave() {
        doTest();
    }

    public void testMultivarInitSingleCaseRightSave() {
        doTest();
    }

    public void testMultivarInitSingleCaseRightNoSave() {
        doTest();
    }

    public void testMultivarInitDoubleCaseLeftSave() {
        doTest();
    }

    public void testMultivarInitDoubleCaseLeftNoSave() {
        doTest();
    }

    public void testMultivarInitDoubleCaseCenterSave() {
        doTest();
    }

    public void testMultivarInitDoubleCaseCenterNoSave() {
        doTest();
    }

    public void testMultivarInitDoubleCaseRightSave() {
        doTest();
    }

    public void testMultivarInitDoubleCaseRightNoSave() {
        doTest();
    }

    public void testNamedParameterSingleUsageSave() {
        doTest();
    }

    public void testNamedParameterSingleUsageNoSave() {
        doTest();
    }

    public void testPositionalParameterSingleUsageSave() {
        doTest();
    }

    public void testPositionalParameterSingleUsageNoSave() {
        doTest();
    }

    public void testStrLiteralInline() {
        doTest();
    }

    public void testStartInline() {
        doTest();
    }

    public void testInlineIntoNamedArg() {
        doTest();
    }

    public void testSingleAssignment() {
        doTest();
    }

    public void testAttributeVariableException() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class,
                     "Cannot perform inline refactoring: attributes of class are used that are not available at inlining location",
                     this::doTest);
    }

    public void testSelfOfOtherClassInExpressionException() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class,
                     "Cannot perform inline refactoring: a reference to `self` is found, but caller and callee are in different classes",
                     this::doTest);
    }

    public void testNoAssignmentException() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class,
                     "Cannot perform inline refactoring: refactoring is supported only when the initializer is present",
                     this::doTest);
    }

    public void testScopeConflictException() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class,
                     "Cannot perform inline refactoring: element from original code is shadowed by another one at inlining location",
                     this::doTest);
    }

    public void testScopeConflictWithRoutineException() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class,
                     "Cannot perform inline refactoring: element from original code is shadowed by another one at inlining location",
                     this::doTest);
    }

    private void doTest() {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        Perl6InlineVariableActionHandler action = new Perl6InlineVariableActionHandler();
        assertTrue(action.isEnabledOnElement(myFixture.getElementAtCaret()));
        action.inlineElement(getProject(), myFixture.getEditor(), myFixture.getElementAtCaret());
        myFixture.checkResultByFile(getTestName(true) + ".p6");
    }
}
