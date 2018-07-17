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

    public void testBracketedDown() {
        myFixture.configureByFile("BlockTestData.p6");
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResultByFile("BlockTestDataDown.p6");
    }

    public void testBracketedCursorFirstUp() {
        myFixture.configureByFile("BlockTestData.p6");
        myFixture.getEditor().getCaretModel().moveToOffset(25);
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("BlockTestDataCursorUp.p6");
    }

    public void testBracketedUp() {
        myFixture.configureByFile("BlockTestData.p6");
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("BlockTestDataUp.p6");
    }

    public void testBracketedUpFirstBlockCursor() {
        myFixture.configureByFile("BlockTestDataBegin.p6");
        myFixture.getEditor().getCaretModel().moveToOffset(24);
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("BlockTestDataBeginUpCursor.p6");
    }

    public void testBracketedUpFirstBlock() {
        myFixture.configureByFile("BlockTestDataBegin.p6");
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("BlockTestDataBeginUp.p6");
    }

    public void testBracketedNestedCursor() {
        myFixture.configureByFile("NestedBrackets.p6");
        myFixture.getEditor().getCaretModel().moveToOffset(45);
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("NestedBracketsUpCursor.p6");
    }

    public void testBracketedNested() {
        myFixture.configureByFile("NestedBrackets.p6");
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("NestedBracketsUp.p6");
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

    public void testCase1() {
        myFixture.configureByFile("Case1.p6");
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResultByFile("Case1Down.p6");
    }

    public void testCase2() {
        myFixture.configureByFile("Case2.p6");
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResultByFile("Case2Down.p6");
    }
}
