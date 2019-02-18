package edument.perl6idea.profiler;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Perl6ProfileTask extends Task.Modal {
    public static final Logger LOG = Logger.getInstance(Perl6ProfileTask.class);
    private File sqlDataFile;
    private Perl6ProfileData myProfileData;

    public Perl6ProfileTask(Project project, String data, boolean canBeCancelled, File file) {
        super(project, data, canBeCancelled);
        sqlDataFile = file;
    }

    @Override
    public void onCancel() {
        if (myProfileData != null)
            myProfileData.cancel();
        sqlDataFile.delete();
    }

    public void run(@NotNull ProgressIndicator indicator) {
        try {
            indicator.setIndeterminate(false);
            // Create in-memory DB
            indicator.setText("Creating a database...");
            indicator.setFraction(0.1);
            myProfileData = new Perl6ProfileData(sqlDataFile.getCanonicalPath());
            indicator.setText("Loading profiler data into the database...");
            indicator.setFraction(0.2);
            myProfileData.initialize();
            indicator.setText("Profile data processing is finished");
            indicator.setFraction(1);
            //createProfileToolWindow();
            ApplicationManager.getApplication().invokeLater(() -> createProfileToolWindow(myProfileData));
        }
        catch (SQLException | IOException e) {
            onCancel();
            LOG.warn(e);
            Notifications.Bus.notify(
                new Notification("Perl 6 Profiler", "Error during profiling data procession",
                                 e.getMessage(), NotificationType.ERROR));
            throw new ProcessCanceledException();
        }
    }

    private void createProfileToolWindow(Perl6ProfileData data) {
        ToolWindow window = ToolWindowManager.getInstance(myProject).registerToolWindow(
            "Perl 6 profiling tool window",
            true,
            ToolWindowAnchor.BOTTOM
        );
        window.setIcon(Perl6Icons.CAMELIA_13x13);
        window.activate(() -> {
            JComponent component = window.getComponent();
            JPanel panel = new JPanel(new BorderLayout());
            Perl6ProfileView view = new Perl6ProfileView(myProject, data);
            panel.add(view.getPanel(), BorderLayout.CENTER);
            component.add(panel);
        });
    }
}