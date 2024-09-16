package org.raku.comma.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ex.SingleConfigurableEditor;
import com.intellij.openapi.options.newEditor.SettingsDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.raku.comma.project.structure.RakuProjectStructureConfigurable;
import org.jetbrains.annotations.NotNull;

public class ShowRakuProjectStructureAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            project = ProjectManager.getInstance().getDefaultProject();
        showDialog(project, e);
    }

    private static void showDialog(Project project, AnActionEvent e) {
        Configurable instance = RakuProjectStructureConfigurable.getInstance(project);
        if (instance != null) {
            new SingleConfigurableEditor(project, instance,
                                         SettingsDialog.DIMENSION_KEY) {
                @NotNull
                @Override
                protected DialogStyle getStyle() {
                    return DialogStyle.COMPACT;
                }
            }.show();
        } else {
            new ShowSecondarySdkSetter().actionPerformed(e);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}
