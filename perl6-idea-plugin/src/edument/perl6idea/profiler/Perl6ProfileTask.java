// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
import com.intellij.ui.components.JBCheckBox;
import edument.perl6idea.Perl6Icons;
import org.intellij.lang.annotations.JdkConstants;
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

public class Perl6ProfileTask extends Task.Modal {
    public static final Logger LOG = Logger.getInstance(Perl6ProfileTask.class);
    private File sqlDataFile;
    private Connection connection = null;
    private static JBCheckBox ourShowInternals;

    public Perl6ProfileTask(Project project, String data, boolean canBeCancelled, File file) {
        super(project, data, canBeCancelled);
        sqlDataFile = file;
    }

    @Override
    public void onCancel() {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException e) {
                LOG.warn(e);
            }
        }
        sqlDataFile.delete();
    }

    public void run(@NotNull ProgressIndicator indicator) {
        try {
            indicator.setIndeterminate(false);
            // Create in-memory DB
            indicator.setText("Creating database...");
            indicator.setFraction(0.1);
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            Statement statement = connection.createStatement();
            indicator.setText("Reading data...");
            indicator.setFraction(0.2);
            Stream<String> lines = Files.lines(Paths.get(sqlDataFile.getCanonicalPath()), StandardCharsets.UTF_8);
            Iterator<String> iterator = lines.iterator();
            // Load a base from the file
            indicator.setText("Preparing database...");
            indicator.setFraction(0.3);
            while (iterator.hasNext()) {
                statement.executeUpdate(iterator.next());
            }

            List<ProfilerNode> nodes = getProfileViewDataRows(statement);

            indicator.setText("Profile data processing is finished");
            indicator.setFraction(1);
            ApplicationManager.getApplication().invokeLater(() -> {
                createProfileToolWindow(nodes);
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

    private void createProfileToolWindow(List<ProfilerNode> nodes) {
        ToolWindow window = ToolWindowManager.getInstance(myProject).registerToolWindow(
            "Perl 6 profiling tool window",
            true,
            ToolWindowAnchor.BOTTOM
        );
        window.setIcon(Perl6Icons.CAMELIA_13x13);
        window.activate(() -> {
            JComponent component = window.getComponent();
            JPanel panel = new JPanel(new BorderLayout());

            panel.add(createOptionsPanel(), BorderLayout.NORTH);

            JXTreeTable treeTable = new Perl6ProfileTreeTable(new Perl6ProfileModel(nodes, ourShowInternals));
            JScrollPane scrollpane = new JScrollPane(treeTable);
            panel.add(scrollpane, BorderLayout.CENTER);

            component.add(panel);
        });
    }

    private static JPanel createOptionsPanel() {
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ourShowInternals = new JBCheckBox("Show internal calls");
        northPanel.add(ourShowInternals);
        return northPanel;
    }

    @NotNull
    private static List<ProfilerNode> getProfileViewDataRows(Statement statement) throws SQLException {
        List<ProfilerNode> nodes = new ArrayList<>();
        ResultSet calls = statement
            .executeQuery("SELECT r.file, r.name, c.inclusive_time, c.exclusive_time, c.entries, json_group_array(sr.name) as callee " +
                          "FROM calls c INNER JOIN calls sc ON sc.parent_id == c.id INNER JOIN routines sr " +
                          "ON sc.routine_id == sr.id INNER JOIN routines r ON c.routine_id == r.id " +
                          "GROUP BY c.id ORDER BY c.inclusive_time DESC");

        while (calls.next()) {
            JSONArray calleeJSON = new JSONArray(calls.getString("callee"));
            List<CalleeNode> callees = new ArrayList<>();
            for (int i = 0; i < calleeJSON.length(); i++) {
                callees.add(new CalleeNode(calleeJSON.getString(i)));
            }
            nodes.add(new ProfilerNode(
                calls.getString("file"),
                calls.getString("name"),
                calls.getInt("inclusive_time"),
                calls.getInt("exclusive_time"),
                calls.getInt("entries"),
                callees
            ));
        }
        return nodes;
    }
}