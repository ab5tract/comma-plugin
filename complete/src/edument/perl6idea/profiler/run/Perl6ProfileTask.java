package edument.perl6idea.profiler.run;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import edument.perl6idea.profiler.ui.Perl6ProfileResultsPanel;
import edument.perl6idea.profiler.ui.Perl6ProfileRoutinesPanel;
import edument.perl6idea.profiler.ui.ProfilerView;
import edument.perl6idea.profiler.model.Perl6ProfileData;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Perl6ProfileTask extends Task.Backgroundable {
    public static final Logger LOG = Logger.getInstance(Perl6ProfileTask.class);
    private final ProfilerView myProfilerView;
    private File sqlDataFile;
    private Perl6ProfileData myProfileData;

    public Perl6ProfileTask(Project project,
                            String data,
                            boolean canBeCancelled,
                            File file,
                            ProfilerView profilerView) {
        super(project, data, canBeCancelled);
        sqlDataFile = file;
        myProfilerView = profilerView;
    }

    @Override
    public void onCancel() {
        // FIXME anything smarter regarding resources handling will be nice here
        if (myProfileData != null) {
            myProfileData.cancel();
            myProfileData = null;
        }
        if (sqlDataFile != null) {
            sqlDataFile.delete();
            sqlDataFile = null;
        }
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        try {
            indicator.setIndeterminate(false);
            // Create in-memory DB
            indicator.setText("Creating a database...");
            indicator.setFraction(0.1);
            indicator.checkCanceled();
            myProfileData = new Perl6ProfileData(sqlDataFile.getCanonicalPath());
            indicator.setText("Loading profiler data into the database...");
            indicator.setFraction(0.2);
            myProfileData.initialize();
            indicator.setText("Profile data processing is finished");
            indicator.setFraction(1);
            ApplicationManager.getApplication().invokeLater(() -> {
                Perl6ProfileResultsPanel view = new Perl6ProfileResultsPanel(myProject, myProfileData);
                myProfilerView.setView(view);
            });
        }
        catch (SQLException | IOException e) {
            onCancel();
            Notifications.Bus.notify(
                new Notification("Raku Profiler", "Error during profiling data procession",
                                 e.getMessage(), NotificationType.ERROR));
            myProfilerView.setView(new JLabel("Could not collect profiling results"));
            throw new ProcessCanceledException();
        } finally {
            if (sqlDataFile != null) {
                sqlDataFile.delete();
                sqlDataFile = null;
            }
        }
    }
}