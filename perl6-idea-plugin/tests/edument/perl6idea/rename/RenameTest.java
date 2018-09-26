package edument.perl6idea.rename;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.NotNull;

public class RenameTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/rename";
    }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    public void testRenameOfPrivateMethodFromName() {
        myFixture.configureByFile("PrivateMethods.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(28);
        myFixture.renameElementAtCaret("!private-method");
        myFixture.checkResultByFile("PrivateMethodsAfter.pm6");
    }

    public void testRenameOfPrivateMethodFromNameWithdash() {
        myFixture.configureByFile("PrivateMethodsWithDash.pm6");
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

    public void testRenameOfTypeFromDeclaration() {
        myFixture.configureByFile("Type.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(10);
        myFixture.renameElementAtCaret("FooNewType");
        myFixture.checkResultByFile("TypeAfter.pm6");
    }

    public void testRenameOfTypeFromUsage() {
        myFixture.configureByFile("Type.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(37);
        myFixture.renameElementAtCaret("FooNewType");
        myFixture.checkResultByFile("TypeAfter.pm6");
    }

    public void testRenameOfTypeFromReturn() {
        myFixture.configureByFile("Type.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(73);
        myFixture.renameElementAtCaret("FooNewType");
        myFixture.checkResultByFile("TypeAfter.pm6");
    }

    public void testRenameOfTypeFromTrait() {
        myFixture.configureByFile("Type.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(86);
        myFixture.renameElementAtCaret("FooNewType");
        myFixture.checkResultByFile("TypeAfter.pm6");
    }

    public void testRenameOfTypeFromIsTrait() {
        myFixture.configureByFile("Type.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(163);
        myFixture.renameElementAtCaret("FooNewType");
        myFixture.checkResultByFile("TypeAfter.pm6");
    }

    public void testRenameOfMultiPartTypeFromUsage() {
        myFixture.configureByFile("MultiPartType.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(45);
        myFixture.renameElementAtCaret("Foo::Baz");
        myFixture.checkResultByFile("MultiPartTypeAfter.pm6");
    }

    // TODO
    //public void testRenameOfMultiPartNestedTypeFromUsage() {
    //    myFixture.configureByFile("MultiPartTypeNested.pm6");
    //    myFixture.getEditor().getCaretModel().moveToOffset(138);
    //    myFixture.renameElementAtCaret("Foo::Bartender");
    //    myFixture.checkResultByFile("MultiPartTypeNestedAfter.pm6");
    //}

    public void testMultiFileSubRename() {
        myFixture.configureByFiles("IdeaFoo/Base.pm6", "IdeaFoo/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(9);
        myFixture.renameElementAtCaret("foo-bar-sub-new");
        myFixture.checkResultByFile("IdeaFoo/Base.pm6", "IdeaFoo/BaseAfter.pm6", true);
        myFixture.checkResultByFile("IdeaFoo/User.pm6", "IdeaFoo/UserAfter.pm6", true);
    }

    public void testLocalVariablesWithExportRename() {
        myFixture.configureByFiles("IdeaFoo2/Base.pm6", "IdeaFoo2/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(36);
        myFixture.renameElementAtCaret("foo-baz");
        myFixture.checkResultByFile("IdeaFoo2/Base.pm6", "IdeaFoo2/BaseAfter.pm6", true);
        myFixture.checkResultByFile("IdeaFoo2/User.pm6", "IdeaFoo2/UserAfter.pm6", true);
    }

    public void testParameterRename() {

    }

    public void testArraySigilVariableRename() {

    }
}
