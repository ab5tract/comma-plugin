package edument.perl6idea.editor;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.NotNull;

public class Perl6EditorMiscTest extends LightCodeInsightFixtureTestCase {
    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    public void testBracketsHasRightIndentOnEnter() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "[<caret>]");
        CommandProcessor.getInstance().executeCommand(getProject(), () -> {
            EditorActionManager actionManager = EditorActionManager.getInstance();
            EditorActionHandler actionHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_ENTER);
            actionHandler.execute(getEditor(), null, DataManager.getInstance().getDataContextFromFocus().getResult());
        }, "", null);
        myFixture.checkResult("[\n    <caret>\n]");
    }
}
