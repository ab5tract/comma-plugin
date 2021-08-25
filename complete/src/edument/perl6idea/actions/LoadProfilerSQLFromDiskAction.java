package edument.perl6idea.actions;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.profiler.run.Perl6ImportRunner;
import edument.perl6idea.run.Perl6ProfileExecutor;
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
            Perl6ImportRunner profile = new Perl6ImportRunner(file);
            Executor executor = new Perl6ProfileExecutor();
            ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder.create(project, executor, profile);
            builder.buildAndExecute();
        }
        catch (ExecutionException e1) {
            Messages.showErrorDialog(project, e1.getMessage(), "Import Failed");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getProject() != null);
    }
}
