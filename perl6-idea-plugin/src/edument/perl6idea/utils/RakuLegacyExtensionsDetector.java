package edument.perl6idea.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.actions.UpdateExtensionsAction;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

public class RakuLegacyExtensionsDetector implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        Module @NotNull [] modules = ModuleManager.getInstance(project).getModules();
        Map<String, List<File>> filesToUpdate = UpdateExtensionsAction.collectFilesWithLegacyNames(modules, false);
        if (!filesToUpdate.isEmpty()) {
            Notification notification = new Notification(
                "raku.misc", Perl6Icons.CAMELIA,
                "Obsolete Raku extensions are detected", "",
                "Obsolete file extensions are detected: " + String.join(", ", filesToUpdate.keySet()),
                NotificationType.WARNING, null);
            notification.addAction(new AnAction("Run Comma Legacy File Rename Tool") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    notification.expire();
                    new UpdateExtensionsAction.RakuChooseExtensionsToUpdateDialog(project, filesToUpdate).show();
                }
            });
            Notifications.Bus.notify(notification);
        }
    }
}
