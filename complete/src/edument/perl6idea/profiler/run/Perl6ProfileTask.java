package edument.perl6idea.profiler.run;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import edument.perl6idea.profiler.ui.Perl6ProfileResultsPanel;
import edument.perl6idea.profiler.ui.ProfilerView;
import edument.perl6idea.profiler.model.Perl6ProfileData;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Perl6ProfileTask extends Task.Backgroundable {
    private final ProfilerView myProfilerView;
    private File sqlDataFile;
    private Perl6ProfileData myProfileData;
    private boolean myHasToRemoveTheFile;

    public Perl6ProfileTask(Project project,
                            String title,
                            boolean canBeCancelled,
                            File file,
                            ProfilerView profilerView, boolean hasToRemoveTheFile) {
        super(project, title, canBeCancelled);
        sqlDataFile = file;
        myProfilerView = profilerView;
        myHasToRemoveTheFile = hasToRemoveTheFile;
    }

    public Perl6ProfileTask(Project project, String title, boolean canBeCancelled, Perl6ProfileData data, ProfilerView view) {
        super(project, title, canBeCancelled);
        myProfileData = data;
        myProfilerView = view;
    }

    @Override
    public void onCancel() {
        // FIXME anything smarter regarding resources handling will be nice here
        if (myProfileData != null) {
            myProfileData.cancel();
            myProfileData = null;
        }
        if (sqlDataFile != null && myHasToRemoveTheFile) {
            sqlDataFile.delete();
            sqlDataFile = null;
        }
    }

    private static String createProfileName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return "Profile results at " + dateFormat.format(date);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        try {
            indicator.setIndeterminate(false);
            // Create in-memory DB
            indicator.setText("Creating a database...");
            indicator.setFraction(0.1);
            indicator.checkCanceled();
            myProfileData = myProfileData == null ? new Perl6ProfileData(myProject, createProfileName(), sqlDataFile) : myProfileData;
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
            if (sqlDataFile != null && myHasToRemoveTheFile) {
                sqlDataFile.delete();
                sqlDataFile = null;
            }
        }
    }
}