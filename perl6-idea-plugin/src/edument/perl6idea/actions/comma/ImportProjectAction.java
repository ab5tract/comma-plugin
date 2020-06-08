package edument.perl6idea.actions.comma;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.impl.welcomeScreen.NewWelcomeScreen;
import org.jetbrains.annotations.NotNull;

public class ImportProjectAction extends ImportModuleAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        doImport(null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (NewWelcomeScreen.isNewWelcomeScreen(e)) {
            e.getPresentation().setIcon(AllIcons.ToolbarDecorator.Import);
            e.getPresentation().setText("Import Project");
        } else {
            e.getPresentation().setText("Project from Existing Sources...");
        }
    }
}
