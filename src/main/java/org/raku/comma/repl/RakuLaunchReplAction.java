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
import org.raku.comma.RakuIcons;
import org.raku.comma.services.project.RakuProjectSdkService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuLaunchReplAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        startRepl(event, null);
    }

    protected void startRepl(@NotNull AnActionEvent event, @Nullable String useModule) {
        if (getSdkHome(event) == null || event.getProject() == null) {
            return;
        }
        Project project = event.getProject();
        String title = useModule != null ? "Raku REPL (use %s)".formatted(useModule) : "Raku REPL";
        RakuReplConsole console = new RakuReplConsole(project, title);
        try {
            console.initAndRun();
            if (useModule != null) {
                ApplicationManager.getApplication()
                                  .invokeAndWait(() -> console.executeStatement("use " + useModule + ";"));
            }
        }
        catch (ExecutionException ex) {
            Notification notification = new Notification("raku.repl.errors", "Cannot run REPL",
                                                         "Could not start Raku REPL", NotificationType.ERROR);
            notification.setIcon(RakuIcons.CAMELIA);
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
    public void update(@NotNull AnActionEvent event) {
        boolean available = getSdkHome(event) != null;
        event.getPresentation().setEnabled(available);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    protected static String getSdkHome(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return null;
        return project.getService(RakuProjectSdkService.class).getSdkPath();
    }
}
