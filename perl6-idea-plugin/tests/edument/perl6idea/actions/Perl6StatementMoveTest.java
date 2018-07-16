package edument.perl6idea.actions;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class Perl6StatementMoveTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/mover";
    }

    public void testSimple() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "say 1;\n<caret>say 2;\nsay 3;\n");
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResult("<caret>say 2;\nsay 1;\nsay 3;\n");

        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "say 1;\n<caret>say 2;\nsay 3;\n");
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResult("say 1;\nsay 3;\n<caret>say 2;\n");
    }

    public void testBracketed() {
    }

    public void testMultilineDown() {
        myFixture.configureByFile("MultilineTestData.p6");
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResultByFile("MultilineTestDataDown.p6");
    }

    public void testMultilineUp() {
        myFixture.configureByFile("MultilineTestData.p6");
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("MultilineTestDataUp.p6");
    }
}
