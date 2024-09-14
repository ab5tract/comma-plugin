package org.raku.profiler.run;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.raku.profiler.ui.RakuProfileResultsPanel;
import org.raku.profiler.ui.ProfilerView;
import org.raku.profiler.model.RakuProfileData;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RakuProfileTask extends Task.Backgroundable {
    private final ProfilerView myProfilerView;
    private File sqlDataFile;
    private RakuProfileData[] myProfileData;
    private boolean myHasToRemoveTheFile;

    public RakuProfileTask(Project project,
                           String title,
                           boolean canBeCancelled,
                           File file,
                           ProfilerView profilerView, boolean hasToRemoveTheFile) {
        super(project, title, canBeCancelled);
        sqlDataFile = file;
        myProfilerView = profilerView;
        myHasToRemoveTheFile = hasToRemoveTheFile;
    }

    public RakuProfileTask(Project project, String title, boolean canBeCancelled, RakuProfileData[] data, ProfilerView view) {
        super(project, title, canBeCancelled);
        myProfileData = data;
        myProfilerView = view;
    }

    @Override
    public void onCancel() {
        // FIXME anything smarter regarding resources handling will be nice here
        if (myProfileData.length > 0) {
            for (RakuProfileData profile : myProfileData) {
                profile.cancel();
            }
            myProfileData = new RakuProfileData[0];
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
            myProfileData = myProfileData == null ? new RakuProfileData[]{
                new RakuProfileData(myProject, createProfileName(), sqlDataFile, myHasToRemoveTheFile)} : myProfileData;
            indicator.setText("Loading profiler data into the database...");
            indicator.setFraction(0.2);
            for (RakuProfileData profile : myProfileData) {
                profile.initialize();
            }
            indicator.setText("Profile data processing is finished");
            indicator.setFraction(1);
            ApplicationManager.getApplication().invokeLater(() -> {
                RakuProfileResultsPanel view = new RakuProfileResultsPanel(myProject, myProfileData);
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