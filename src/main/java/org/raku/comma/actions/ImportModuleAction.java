package org.raku.comma.actions;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.comma.project.RakuProjectImportProvider;
import org.raku.comma.project.projectWizard.CommaAbstractProjectWizard;
import org.raku.comma.project.projectWizard.CommaAddModuleWizard;
import org.raku.comma.utils.CommaProjectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.file.Paths;

public class ImportModuleAction extends AnAction {
    private static final String LAST_IMPORTED_LOCATION = "comma.last.imported.location";

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        doImport(getEventProject(e));
    }

    public static void doImport(@Nullable Project project) {
        CommaAddModuleWizard wizard = selectFileAndCreateWizard(project, null);

        if (wizard != null && wizard.getStepCount() > 0 && wizard.showAndGet())
            createFromWizard(project, wizard);
    }

    public static void createFromWizard(@Nullable Project project, CommaAbstractProjectWizard wizard) {
        try {
            doCreateFromWizard(project, wizard);
        }
        finally {
            wizard.disposeIfNeeded();
        }
    }

    private static void doCreateFromWizard(@Nullable Project project, CommaAbstractProjectWizard wizard) {
        final ProjectBuilder projectBuilder = wizard.getProjectBuilder();
        if (project == null) {
            CommaProjectUtil.createFromWizard(wizard);
            return;
        }

        try {
            if (wizard.getStepCount() > 0) {
                new NewModuleAction().createModuleFromWizard(project, wizard);
            }
            else {
                if (projectBuilder.validate(project, project)) {
                    projectBuilder.commit(project);
                }
            }
        }
        finally {
            if (projectBuilder != null) {
                projectBuilder.cleanup();
            }
        }
    }

    @Nullable
    public static CommaAddModuleWizard selectFileAndCreateWizard(@Nullable Project project, @Nullable Component dialogParent) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
        descriptor.setHideIgnored(false);
        descriptor.setTitle("Select File or Directory to Import");
        descriptor.setDescription(RakuProjectImportProvider.getDescription());
        return selectFileAndCreateWizard(project, dialogParent, descriptor);
    }

    @Nullable
    public static CommaAddModuleWizard selectFileAndCreateWizard(@Nullable Project project,
                                                                 @Nullable Component dialogParent,
                                                                 @NotNull FileChooserDescriptor descriptor) {
        FileChooserDialog chooser = FileChooserFactory.getInstance().createFileChooser(descriptor, project, dialogParent);
        VirtualFile toSelect = null;
        String lastLocation = PropertiesComponent.getInstance().getValue(LAST_IMPORTED_LOCATION);
        if (lastLocation != null) {
            toSelect = LocalFileSystem.getInstance().refreshAndFindFileByPath(lastLocation);
        }
        VirtualFile[] files = chooser.choose(project, toSelect);
        if (files.length == 0) {
            return null;
        }

        final VirtualFile file = files[0];
        if (project == null) { // wizard will create a new project
            for (Project p : ProjectManager.getInstance().getOpenProjects()) {
                if (ProjectUtil.isSameProject(Paths.get(file.getPath()), p)) {
                    ProjectUtil.focusProjectWindow(p, false);
                    return null;
                }
            }
        }
        PropertiesComponent.getInstance().setValue(LAST_IMPORTED_LOCATION, file.getPath());
        return createImportWizard(project, dialogParent, file);
    }

    @Nullable
    public static CommaAddModuleWizard createImportWizard(@Nullable final Project project,
                                                          @Nullable Component dialogParent,
                                                          @NotNull final VirtualFile file) {
        RakuProjectImportProvider provider = new RakuProjectImportProvider();
        if (!file.isDirectory() && !provider.canImportFromFile(file)) {
            Messages.showInfoMessage(project, "Cannot import anything from " + file.getPath(),
                                     "Cannot Import");
            return null;
        }

        String path = file.isDirectory() ? file.getPath() : file.getParent().getPath();
        return dialogParent == null
               ? new CommaAddModuleWizard(project, path, provider)
               : new CommaAddModuleWizard(project, dialogParent, path, provider);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}
