package edument.perl6idea.formatter;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class FormatterTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/formatter";
  }

    public void testBasicFormatting() {
        myFixture.configureByFiles("basic.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("basic.out.p6");
    }

    public void testAssortedFormatting() {
        myFixture.configureByFiles("assorted.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("assorted.out.p6");
    }

    public void testBasicGrammarFormatting() {
        myFixture.configureByFiles("grammar-basic.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("grammar-basic.out.p6");
    }


    public void testContinuationAfterBlock() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "{\n\n}<caret>");

        CommandProcessor.getInstance().executeCommand(getProject(), new Runnable() {
            @Override
            public void run() {
                EditorActionManager actionManager = EditorActionManager.getInstance();
                EditorActionHandler actionHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_ENTER);
                actionHandler.execute(getEditor(), DataManager.getInstance().getDataContext());
            }
        }, "", null);
        myFixture.checkResult("{\n\n}\n<caret>");
    }

    public void testLineBreakingOfStatements() {
        myFixture.configureByFiles("break-lines.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("break-lines.out.p6");
    }

    public void testLineBreakingOfBlocks() {
        myFixture.configureByFiles("blocks.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("blocks.out.p6");
    }

    public void testRemoveSpaceBeforeSemi() {
        myFixture.configureByFiles("space-before-semi.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("space-before-semi.out.p6");
    }

    public void testMultilineHashFormatting() {
        myFixture.configureByFiles("hash.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("hash.out.p6");
    }
}
