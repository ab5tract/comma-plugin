package edument.perl6idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ex.SingleConfigurableEditor;
import com.intellij.openapi.options.newEditor.SettingsDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ui.configuration.ProjectStructureConfigurable;
import edument.perl6idea.project.structure.Perl6ProjectStructureConfigurable;
import org.jetbrains.annotations.NotNull;

public class ShowPerl6ProjectStructureAction extends AnAction {
    @Override
    public boolean startInTransaction() {
        return true;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            project = ProjectManager.getInstance().getDefaultProject();
        showDialog(project);
    }

    private static void showDialog(Project project) {
        Configurable instance = Perl6ProjectStructureConfigurable.getInstance(project);
        if (instance == null)
            instance = ProjectStructureConfigurable.getInstance(project);
        new SingleConfigurableEditor(project, instance,
                                     SettingsDialog.DIMENSION_KEY) {
            @NotNull
            @Override
            protected DialogStyle getStyle() {
                return DialogStyle.COMPACT;
            }
        }.show();
    }
}
