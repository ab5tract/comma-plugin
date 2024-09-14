package edument.rakuidea.cro.rename;

import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.cro.template.CroTemplateFileType;

public class RenameTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/ctl-rename";
    }

    private void doTest(int offset, String newName, String result) {
        myFixture.configureByText(CroTemplateFileType.INSTANCE, "<:sub fofo($title)><$title.attr></:sub><&fofo><:macro page></:macro><|page>");
        myFixture.getEditor().getCaretModel().moveToOffset(offset);
        myFixture.renameElementAtCaret(newName);
        myFixture.checkResult(result);
    }

    public void testRenameOfVariable() {
        doTest(15, "$name", "<:sub fofo($name)><$name.attr></:sub><&fofo><:macro page></:macro><|page>");
        doTest(22, "$name", "<:sub fofo($name)><$name.attr></:sub><&fofo><:macro page></:macro><|page>");
    }

    public void testRenameOfSubroutine() {
        doTest(8, "toto", "<:sub toto($title)><$title.attr></:sub><&toto><:macro page></:macro><|page>");
        doTest(42, "toto", "<:sub toto($title)><$title.attr></:sub><&toto><:macro page></:macro><|page>");
    }

    public void testRenameOfMacro() {
        doTest(57, "page2", "<:sub fofo($title)><$title.attr></:sub><&fofo><:macro page2></:macro><|page2>");
        doTest(72, "page2", "<:sub fofo($title)><$title.attr></:sub><&fofo><:macro page2></:macro><|page2>");
    }

    public void testCrossFileRename() {
        myFixture.configureByFiles("IdeaFoo/base.crotmp", "IdeaFoo/user.crotmp");
        myFixture.renameElementAtCaret("re-named");
        myFixture.checkResultByFile("IdeaFoo/base.crotmp", "IdeaFoo/base.after.crotmp", true);
        myFixture.checkResultByFile("IdeaFoo/user.crotmp", "IdeaFoo/user.after.crotmp", true);
    }
}
