package edument.perl6idea.actions;

import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class Perl6StatementMoveTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/mover";
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

    public void testCase3() {
        myFixture.configureByFile("Case3.p6");
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("Case3Up.p6");
    }

    public void testCase4() {
        myFixture.configureByFile("Case4.p6");
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("Case4Up.p6");
    }

    public void testCase5() {
        myFixture.configureByFile("Case5.p6");
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("Case5.p6");
    }

    public void testCase6() {
        myFixture.configureByFile("Case6.p6");
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("Case6Up.p6");
    }

    public void testCase7() {
        myFixture.configureByFile("Case7.p6");
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResultByFile("Case7Down.p6");
    }

    public void testCase8() {
        myFixture.configureByFile("Case8.p6");
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResultByFile("Case8Down.p6");
    }

    public void testCase9() {
        myFixture.configureByFile("Case9.p6");
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResultByFile("Case9Down.p6");
    }

    public void testCase10() {
        myFixture.configureByFile("Case10.p6");
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResultByFile("Case10Down.p6");
    }

    public void testCase10Up() {
        myFixture.configureByFile("Case10.p6");
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("Case10Up.p6");
    }

    public void testHeredocEdgeCases() {
        myFixture.configureByFile("Heredoc1.p6");
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("Heredoc1.p6");
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResultByFile("Heredoc1.p6");
        myFixture.getEditor().getCaretModel().moveToOffset(100);
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("Heredoc1Cursor.p6");
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResultByFile("Heredoc1Cursor.p6");
    }

    public void testHeredocDown() {
        myFixture.configureByFile("Heredoc2.p6");
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResultByFile("Heredoc2Down.p6");
    }

    public void testHeredocDownCursor() {
        myFixture.configureByFile("Heredoc2.p6");
        myFixture.getEditor().getCaretModel().moveToOffset(100);
        myFixture.performEditorAction("MoveStatementDown");
        myFixture.checkResultByFile("Heredoc2Down.p6");
    }

    public void testHeredocUp() {
        myFixture.configureByFile("Heredoc3.p6");
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("Heredoc3Up.p6");
    }

    public void testHeredocUpCursor() {
        myFixture.configureByFile("Heredoc3.p6");
        myFixture.getEditor().getCaretModel().moveToOffset(100);
        myFixture.performEditorAction("MoveStatementUp");
        myFixture.checkResultByFile("Heredoc3Up.p6");
    }
}
