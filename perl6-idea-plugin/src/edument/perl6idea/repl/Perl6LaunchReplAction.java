package edument.perl6idea.repl;

import com.intellij.execution.ExecutionException;
import com.intellij.notification.Notification;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.NotNull;

public class Perl6LaunchReplAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Sdk sdk = getSdk(e);
        if (sdk == null)
            return;
        Project project = e.getProject();
        Perl6ReplConsole console = new Perl6ReplConsole(project, "Raku REPL", project.getBasePath());
        try {
            console.initAndRun();
        }
        catch (ExecutionException ex) {
            // TODO report
            ex.printStackTrace();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean available = getSdk(e) != null;
        e.getPresentation().setEnabled(available);
    }

    private static Sdk getSdk(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return null;
        return ProjectRootManager.getInstance(project).getProjectSdk();
    }
}
