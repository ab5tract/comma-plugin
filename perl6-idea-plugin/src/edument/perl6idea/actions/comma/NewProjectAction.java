package edument.perl6idea.actions.comma;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.wm.impl.welcomeScreen.NewWelcomeScreen;
import edument.perl6idea.project.projectWizard.CommaNewProjectWizard;
import edument.perl6idea.utils.CommaProjectUtil;
import org.jetbrains.annotations.NotNull;

public class NewProjectAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CommaNewProjectWizard wizard = new CommaNewProjectWizard(null, ModulesProvider.EMPTY_MODULES_PROVIDER, null);
        CommaProjectUtil.createNewProject(wizard);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (NewWelcomeScreen.isNewWelcomeScreen(e)) {
            e.getPresentation().setIcon(AllIcons.Welcome.CreateNewProject);
        }

        String actionText = NewWelcomeScreen.isNewWelcomeScreen(e) ? "Create New Project" : "Project...";
        e.getPresentation().setText(actionText);
    }
}
