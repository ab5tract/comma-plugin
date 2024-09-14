package org.raku.comma.actions;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.DefaultModulesProvider;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.comma.project.projectWizard.CommaAbstractProjectWizard;
import org.raku.comma.project.projectWizard.CommaNewProjectWizard;
import org.raku.comma.project.structure.RakuModulesConfigurator;
import org.jetbrains.annotations.NotNull;

public class NewModuleAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = getEventProject(e);
        if (project == null) {
            return;
        }

        String defaultPath = null;
        final VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (virtualFile != null && virtualFile.isDirectory()) {
            defaultPath = virtualFile.getPath();
        }
        CommaNewProjectWizard wizard = new CommaNewProjectWizard(project, new DefaultModulesProvider(project), defaultPath);

        if (wizard.showAndGet()) {
            createModuleFromWizard(project, wizard);
        }
    }

    public void createModuleFromWizard(Project project, CommaAbstractProjectWizard wizard) {
        final ProjectBuilder builder = wizard.getBuilder(project);
        if (builder == null) return;
        if (builder instanceof ModuleBuilder) {
            ((ModuleBuilder)builder).commitModule(project, null);
            return;
        }
        else {
            builder.commit(project, null, new DefaultModulesProvider(project));
            if (builder.isOpenProjectSettingsAfter()) {
                RakuModulesConfigurator.showDialog(project, null, null);
            }
        }
        project.save();
    }
}
