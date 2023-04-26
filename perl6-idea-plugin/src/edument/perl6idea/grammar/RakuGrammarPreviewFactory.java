package edument.perl6idea.grammar;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;

public class RakuGrammarPreviewFactory implements ToolWindowFactory {
    public static final String PREVIEW_WINDOW_ID = "Raku Grammar Preview";

    @Override
    public void init(ToolWindow window) {
        window.setIcon(Perl6Icons.CAMELIA_13x13);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        if (ApplicationManager.getApplication().isUnitTestMode()) return;
        RakuGrammarPreviewer previewer = new RakuGrammarPreviewer(project);
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(previewer, "", false);
        content.setCloseable(false);
        toolWindow.getContentManager().addContent(content);
    }
}
