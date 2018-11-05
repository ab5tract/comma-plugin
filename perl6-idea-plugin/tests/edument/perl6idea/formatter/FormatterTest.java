package edument.perl6idea.formatter;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

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
}
