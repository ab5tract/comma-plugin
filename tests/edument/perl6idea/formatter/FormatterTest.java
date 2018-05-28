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
}
