package org.raku.actions;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.profiler.run.RakuImportRunner;
import org.raku.run.RakuProfileExecutor;
import org.jetbrains.annotations.NotNull;

public class LoadProfilerSQLFromDiskAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        assert project != null;
        FileChooserDescriptor sqlDescriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
            .withFileFilter(vf -> vf.getName().endsWith(".sql"));
        sqlDescriptor.setTitle("Choose a File with SQL Data from the Raku Profiler");
        VirtualFile file = FileChooser.chooseFile(sqlDescriptor, project, null);
        if (file == null)
            return;

        executeTheFile(project, file);
    }

    private static void executeTheFile(Project project, VirtualFile file) {
        try {
            RakuImportRunner profile = new RakuImportRunner(file);
            Executor executor = new RakuProfileExecutor();
            ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder.create(project, executor, profile);
            builder.buildAndExecute();
        }
        catch (ExecutionException e1) {
            Messages.showErrorDialog(project, e1.getMessage(), "Import Failed");
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getProject() != null);
    }
}
