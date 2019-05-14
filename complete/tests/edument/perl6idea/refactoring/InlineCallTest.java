package edument.perl6idea.refactoring;

import com.intellij.ide.DataManager;
import com.intellij.refactoring.inline.InlineRefactoringActionHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

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

    public void testMultireturnBlockInlineFails() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class,
                "Refactoring is not supported when return statement interrupts the execution flow",
                this::doTest);
    }

    public void testSingleReturnMulticallSave() {
        doTest();
    }

    public void testSingleReturnMulticallNoSave() {
        doTest();
    }

    public void testNonTrailingReturnFails() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class,
                "Refactoring is not supported when return statement interrupts the execution flow",
                this::doTest);
    }

    public void testStateVariableFails() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class,
                "Refactoring is not supported when state variables are present",
                this::doTest);
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

// TODO
//    public void testRejectIncompleteCall() {
//        doTest();
//    }
//    public void testRejectIncompleteNamedCall() {
//        doTest();
//    }
//
//    public void testRejectSlurpyCall() {
//        doTest();
//    }

    private void doTest() {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        InlineRefactoringActionHandler action = new InlineRefactoringActionHandler();
        action.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(),
                DataManager.getInstance().getDataContextFromFocus().getResult());
        myFixture.checkResultByFile(getTestName(true) + ".p6");
    }
}
