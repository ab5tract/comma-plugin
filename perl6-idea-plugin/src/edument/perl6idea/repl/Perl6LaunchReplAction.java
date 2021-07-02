package edument.perl6idea.repl;

import com.intellij.execution.ExecutionException;
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
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.services.Perl6BackupSDKService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6LaunchReplAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        startRepl(e, null);
    }

    protected void startRepl(@NotNull AnActionEvent e, @Nullable String useModule) {
        if (getSdkHome(e) == null || e.getProject() == null)
            return;
        Project project = e.getProject();
        Perl6ReplConsole console = new Perl6ReplConsole(project, "Raku REPL", project.getBasePath());
        try {
            console.initAndRun();
            if (useModule != null)
                ApplicationManager.getApplication().invokeAndWait(() -> console.executeStatement("use " + useModule + ";"));
        }
        catch (ExecutionException ex) {
            Notification notification = new Notification("raku.repl.errors", Perl6Icons.CAMELIA, "Cannot run REPL",
                                                         "", "Could not start Raku REPL", NotificationType.ERROR, null);
            notification = notification.addAction(new AnAction("Check SDK") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    new ShowPerl6ProjectStructureAction().actionPerformed(e);
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
        boolean available = getSdkHome(e) != null;
        e.getPresentation().setEnabled(available);
    }

    protected static String getSdkHome(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return null;
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (sdk != null && sdk.getSdkType() instanceof Perl6SdkType) {
            return sdk.getHomePath();
        } else {
            return project.getService(Perl6BackupSDKService.class).getProjectSdkPath(project.getProjectFilePath());
        }
    }
}
