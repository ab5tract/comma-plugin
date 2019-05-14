package edument.perl6idea.refactoring;

import com.intellij.ide.DataManager;
import com.intellij.refactoring.BaseRefactoringProcessor;
import com.intellij.refactoring.inline.InlineRefactoringActionHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class InlineVariableTest extends LightPlatformCodeInsightFixtureTestCase {
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
            "Inline Variable refactoring is supported only when the initializer is present", this::doTest);
    }

    public void testIntermediateAssignmentConflict() {
        assertThrows(BaseRefactoringProcessor.ConflictsInTestsException.class,
            "Variable to be inlined has occurrences as lvalue", this::doTest);
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

    public void testNoAssignmentException() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, "Inline Variable refactoring is supported only when the initializer is present", this::doTest);
    }

    private void doTest() {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        InlineRefactoringActionHandler action = new InlineRefactoringActionHandler();
        action.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), DataManager.getInstance().getDataContextFromFocus().getResult());
        myFixture.checkResultByFile(getTestName(true) + ".p6");
    }
}
