package edument.perl6idea.formatter;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.function.BiFunction;

public class FormatterTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/formatter";
    }

    public void testBasicFormatting() {
        doTest("basic");
    }

    public void testAssortedFormatting() {
        doTest("assorted");
    }

    public void testBasicGrammarFormatting() {
        doTest("grammar-basic");
    }

    public void testLineBreakingOfStatements() {
        doTest("break-lines");
    }

    public void testLineBreakingOfBlocks() {
        doTest("blocks", (s1, s2) -> {
            s1.KEEP_SIMPLE_BLOCKS_IN_ONE_LINE = false;
            return true;
        });
    }

    public void testRemoveSpaceBeforeSemi() {
        doTest("space-before-semi");
    }

    public void testMultilineHashFormatting() {
        doTest("hash");
    }

    public void testMultilineHashWithMultilineValueFormatting() {
        doTest("hash-multiline-values");
    }

    public void testMultilineArrayFormatting() {
        doTest("array", (s1, s2) -> {
            s1.getIndentOptions().CONTINUATION_INDENT_SIZE = 4;
            return true;
        });
    }

    public void testTrailingCommaInArrayAndHashFormatting() {
        doTest("trailing-comma");
    }

    public void testCommentsNotBrokenByFormatting() {
        doTest("comments-left-intact");
    }

    private void doTest(String input, String output) {
        doTest(input, output, (s1, s2) -> true);
    }

    private void doTest(String input, String output, BiFunction<CommonCodeStyleSettings, Perl6CodeStyleSettings, Boolean> config) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, input);
        reformat(config);
        myFixture.checkResult(output);
    }

    private void doTest(String filename) {
        doTest(filename, (s1, s2) -> true);
    }

    private void doTest(String filename, BiFunction<CommonCodeStyleSettings, Perl6CodeStyleSettings, Boolean> config) {
        myFixture.configureByFiles(filename + ".in.p6");
        reformat(config);
        myFixture.checkResultByFile(filename + ".out.p6");
    }

    private void reformat(BiFunction<CommonCodeStyleSettings, Perl6CodeStyleSettings, Boolean> config) {
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            CodeStyleSettingsManager settingsManager = CodeStyleSettingsManager.getInstance(myFixture.getProject());
            CodeStyleSettings temp = settingsManager.getTemporarySettings();
            CommonCodeStyleSettings commons = temp.getCommonSettings(Perl6Language.INSTANCE);
            Perl6CodeStyleSettings customs = temp.getCustomSettings(Perl6CodeStyleSettings.class);
            config.apply(commons, customs);
            settingsManager.setTemporarySettings(temp);
            codeStyleManager.reformat(myFixture.getFile());
            settingsManager.dropTemporarySettings();
        });
    }
}
