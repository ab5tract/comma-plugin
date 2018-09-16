package edument.perl6idea.profiler;

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
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import edument.perl6idea.Perl6Icons;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ProfileTerminationListener extends ProcessAdapter {
    public static final Logger LOG = Logger.getInstance(ProfileTerminationListener.class);
    private final File file;
    private final Project project;

    ProfileTerminationListener(File file, Project project) {
        this.file = file;
        this.project = project;
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        if (event.getExitCode() != 0) {
            file.delete();
            return;
        }
        final Connection[] connection = {null};

        ProgressManager.getInstance().run(new Task.Modal(project, "Processing Profiling Data", true) {
            @Override
            public void onCancel() {
                if (connection[0] != null) {
                    try {
                        connection[0].close();
                    }
                    catch (SQLException e) {
                        LOG.warn(e);
                    }
                }
                file.delete();
            }

            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(false);
                    // Create in-memory DB
                    indicator.setText("Creating database...");
                    indicator.setFraction(0.1);
                    connection[0] = DriverManager.getConnection("jdbc:sqlite::memory:");
                    Statement statement = connection[0].createStatement();
                    indicator.setText("Reading data...");
                    indicator.setFraction(0.2);
                    Stream<String> lines = Files.lines(Paths.get(file.getCanonicalPath()), StandardCharsets.UTF_8);
                    Iterator<String> iterator = lines.iterator();
                    // Load base
                    indicator.setText("Preparing database...");
                    indicator.setFraction(0.3);
                    while (iterator.hasNext())
                        statement.executeUpdate(iterator.next());
                    indicator.setText("Done!");
                    indicator.setFraction(1);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        ToolWindow window = ToolWindowManager.getInstance(project).registerToolWindow(
                            "Perl 6 profiling tool window",
                            true,
                            ToolWindowAnchor.BOTTOM
                        );
                        window.setIcon(Perl6Icons.CAMELIA_13x13);
                        window.activate(() -> {
                            JComponent component = window.getComponent();
                            List<ProfilerNode> routines = new ArrayList<>();
                            routines.add(new ProfilerNode("Foo", 50, 50, 1));
                            routines.add(new ProfilerNode("Bar", 50, 50, 1));
                            TreeTableModel treeTableModel = new Perl6ProfileModel(routines);
                            JXTreeTable treeTable = new JXTreeTable(treeTableModel);
                            JScrollPane scrollpane = new JScrollPane(treeTable);
                            component.add(scrollpane);
                        });
                    });
                }
                catch (SQLException | IOException e) {
                    LOG.warn(e);
                    Notifications.Bus.notify(
                        new Notification("Perl 6 Profiler", "Error during profiling data procession",
                                         e.getMessage(), NotificationType.ERROR));
                    throw new ProcessCanceledException();
                }
                finally {
                    onCancel();
                }
            }
        });
    }
}
