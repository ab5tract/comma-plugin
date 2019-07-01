package edument.perl6idea.editor;

import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor;
import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessors;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.Perl6LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Perl6SmartEnterTest extends LightCodeInsightFixtureTestCase {
    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/smartEnter";
    }

    public void doTest() {
        myFixture.configureByFile(getTestName(false) + ".p6");
        final List<SmartEnterProcessor> processors = SmartEnterProcessors.INSTANCE.forKey(Perl6Language.INSTANCE);
        new WriteCommandAction(myFixture.getProject()) {
            @Override
            protected void run(@NotNull Result result) {
                final Editor editor = myFixture.getEditor();
                for (SmartEnterProcessor processor : processors) {
                    processor.process(myFixture.getProject(), editor, myFixture.getFile());
                }
            }
        }.execute();
        myFixture.checkResultByFile(getTestName(false) + "_after.p6", true);
    }

    public void testStatementAfterCall() {
        doTest();
    }

    public void testStatementAfterCallWithSemicolon() {
        doTest();
    }

    public void testMultilineStatement() {
        doTest();
    }

    public void testMultilineStatementWithSemicolon() {
        doTest();
    }

    public void testClassAfterName() {
        doTest();
    }

    public void testPackageAfterName() {
        doTest();
    }

    public void testClassAfterTrait() {
        doTest();
    }

    public void testClassAfterWhiteSpace() {
        doTest();
    }

    public void testRoleAfterSignature() {
        doTest();
    }

    public void testClassAfterBadCharacter() {
        doTest();
    }

    public void testClassAfterSemiBlockoid() {
        doTest();
    }

    public void testClassAfterBlockoid() {
        doTest();
    }

    public void testScopedClass() {
        doTest();
    }

    public void testUnit() {
        doTest();
    }

    public void testRoutineSignature() {
        doTest();
    }

    public void testRoutineOutsideSignature() {
        doTest();
    }

    public void testWhitespaceIsNotCaptured() {
        doTest();
    }

    public void testIfHandling() {
        doTest();
    }

    public void testElseHandling() {
        doTest();
    }

    public void testWith() {
        doTest();
    }

    public void testOrWith() {
        doTest();
    }

    public void testUnless() {
        doTest();
    }

    public void testFor() {
        doTest();
    }

    public void testFor2() {
        doTest();
    }

    public void testGiven() {
        doTest();
    }

    public void testGivenWhen() {
        doTest();
    }

    public void testLoopCondition() {
        doTest();
    }

    public void testLoopOutside() {
        doTest();
    }

    public void testInsideOfBlock() {
        doTest();
    }

    public void testLoopInsideOfBlock() {
        doTest();
    }
}
