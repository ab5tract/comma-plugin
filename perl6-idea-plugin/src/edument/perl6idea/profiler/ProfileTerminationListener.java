package edument.perl6idea.profiler;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import edument.perl6idea.Perl6Icons;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ProfileTerminationListener extends ProcessAdapter {
    public static final Logger LOG = Logger.getInstance(ProfileTerminationListener.class);
    private final File file;
    private final Project project;
    private Exception myException = null;

    ProfileTerminationListener(File file, Project project) {
        this.file = file;
        this.project = project;
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        if (event.getExitCode() != 0) {
            file.delete();
            LOG.warn(new ExecutionException("The executed process has finished with an error, cannot show the profiling data."));
        } else {
            ProgressManager.getInstance().run(new Perl6ProfileTask(project, "Processing Profiling Data", true, file));
        }
    }

    public Exception getException() {
        return myException;
    }
}
