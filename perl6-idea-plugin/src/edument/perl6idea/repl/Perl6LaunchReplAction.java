package edument.perl6idea.repl;

import com.intellij.execution.ExecutionException;
import com.intellij.ide.ApplicationActivationStateManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.actions.ShowPerl6ProjectStructureAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6LaunchReplAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        startRepl(e, null);
    }

    protected void startRepl(@NotNull AnActionEvent e, @Nullable String useModule) {
        Sdk sdk = getSdk(e);
        if (sdk == null)
            return;
        Project project = e.getProject();
        Perl6ReplConsole console = new Perl6ReplConsole(project, "Raku REPL", project.getBasePath());
        try {
            console.initAndRun();
            if (useModule != null)
                ApplicationManager.getApplication().invokeLater(() -> {
                    console.executeStatement("use " + useModule + ";");
                });
        }
        catch (ExecutionException ex) {
            Notification notification = new Notification("Raku REPL errors", Perl6Icons.CAMELIA, "Cannot run REPL",
                                                         "", "Could not start Raku REPL", NotificationType.ERROR, null);
            notification = notification.addAction(new AnAction("Check SDK") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    new ShowPerl6ProjectStructureAction().actionPerformed(e);
                }

                @Override
                public boolean startInTransaction() {
                    return true;
                }
            });
            notification = notification.addAction(new AnAction("Show Exception Details to Report") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    Logger.getInstance(Perl6LaunchReplAction.class).error(ex);
                }
            });
            Notifications.Bus.notify(notification, project);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean available = getSdk(e) != null;
        e.getPresentation().setEnabled(available);
    }

    protected static Sdk getSdk(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return null;
        return ProjectRootManager.getInstance(project).getProjectSdk();
    }
}
