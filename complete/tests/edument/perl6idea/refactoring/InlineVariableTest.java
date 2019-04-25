package edument.perl6idea.refactoring;

import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import edument.perl6idea.refactoring.inline.Perl6InlineHandler;

public class InlineVariableTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "complete/testData/inline-variable";
    }

    public void testInitSingleUsage() {
        doTest();
    }

    public void testInitManyUsages() {
        doTest();
    }

    public void testLateSingleInitSingleCase() {
        doTest();
    }

    public void testMultivarInitSingleCaseLeft() {
        doTest();
    }

    public void testMultivarInitSingleCaseRight() {
        doTest();
    }

    public void testMultivarInitDoubleCaseLeft() {
        doTest();
    }

    public void testMultivarInitDoubleCaseCenter() {
        doTest();
    }

    public void testMultivarInitDoubleCaseRight() {
        doTest();
    }

    public void testNamedParameterSingleUsage() {
        doTest();
    }

    public void testPositionalParameterSingleUsage() {
        doTest();
    }

    public void testNoAssignmentException() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, "Cannot inline the variable $foo: the variable is not initialized", this::doTest);
    }

    private void doTest() {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        Perl6InlineHandler handler = new Perl6InlineHandler();
        handler.inlineElement(getProject(), myFixture.getEditor(), myFixture.getElementAtCaret());
        myFixture.checkResultByFile(getTestName(true) + ".p6");
    }
}
