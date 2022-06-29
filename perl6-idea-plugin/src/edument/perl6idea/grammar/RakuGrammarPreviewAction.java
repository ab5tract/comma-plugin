package edument.perl6idea.grammar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import edument.perl6idea.module.Perl6ModuleType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RakuGrammarPreviewAction extends AnAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        boolean isRakuProject = false;
        if (project != null) {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            for (Module module : modules) {
                if (Objects.equals(Perl6ModuleType.ID, module.getModuleTypeName())) {
                    isRakuProject = true;
                    break;
                }
            }
        }
        e.getPresentation().setEnabledAndVisible(isRakuProject);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            throw new UnsupportedOperationException();
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow window = toolWindowManager.getToolWindow(RakuGrammarPreviewFactory.PREVIEW_WINDOW_ID);
        if (window != null)
            window.show(null);
    }
}
