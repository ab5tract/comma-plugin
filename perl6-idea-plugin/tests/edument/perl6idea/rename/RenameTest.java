package edument.perl6idea.rename;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class RenameTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/rename";
    }

    public void testRenameOfPrivateMethodFromName() {
        myFixture.configureByFile("PrivateMethods.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(28);
        myFixture.renameElementAtCaret("!private-method");
        myFixture.checkResultByFile("PrivateMethodsAfter.pm6");
    }

    public void testRenameOfPrivateMethodFromCall() {
        myFixture.configureByFile("PrivateMethods.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(136);
        myFixture.renameElementAtCaret("!private-method");
        myFixture.checkResultByFile("PrivateMethodsAfter.pm6");
    }

    public void testRenameOfSubFromName() {
        myFixture.configureByFile("Subroutines.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(5);
        myFixture.renameElementAtCaret("renamed-sub");
        myFixture.checkResultByFile("SubroutinesAfter.pm6");
    }

    public void testRenameOfSubFromCall() {
        myFixture.configureByFile("Subroutines.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(47);
        myFixture.renameElementAtCaret("renamed-sub");
        myFixture.checkResultByFile("SubroutinesAfter.pm6");
    }

    public void testRenameOfSubFromIndirectName() {
        myFixture.configureByFile("Subroutines.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(71);
        myFixture.renameElementAtCaret("renamed-sub");
        myFixture.checkResultByFile("SubroutinesAfter.pm6");
    }
}
