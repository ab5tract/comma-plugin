package org.raku.comma.repl;

import com.intellij.execution.ExecutionException;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import org.raku.comma.RakuIcons;
import org.raku.comma.actions.ShowRakuProjectStructureAction;
import org.raku.comma.sdk.RakuSdkType;
import org.raku.comma.services.RakuBackupSDKService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuLaunchReplAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        startRepl(e, null);
    }

    protected void startRepl(@NotNull AnActionEvent e, @Nullable String useModule) {
        if (getSdkHome(e) == null || e.getProject() == null)
            return;
        Project project = e.getProject();
        RakuReplConsole console = new RakuReplConsole(project, "Raku REPL", project.getBasePath());
        try {
            console.initAndRun();
            if (useModule != null)
                ApplicationManager.getApplication().invokeAndWait(() -> console.executeStatement("use " + useModule + ";"));
        }
        catch (ExecutionException ex) {
            Notification notification = new Notification("raku.repl.errors", "Cannot run REPL",
                                                         "Could not start Raku REPL", NotificationType.ERROR);
            notification.setIcon(RakuIcons.CAMELIA);
            notification = notification.addAction(new AnAction("Check SDK") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    new ShowRakuProjectStructureAction().actionPerformed(e);
                }
            });
            notification = notification.addAction(new AnAction("Show Exception Details to Report") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    Logger.getInstance(RakuLaunchReplAction.class).error(ex);
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

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    protected static String getSdkHome(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return null;
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (sdk != null && sdk.getSdkType() instanceof RakuSdkType) {
            return sdk.getHomePath();
        } else {
            return project.getService(RakuBackupSDKService.class).getProjectSdkPath(project.getProjectFilePath());
        }
    }
}
