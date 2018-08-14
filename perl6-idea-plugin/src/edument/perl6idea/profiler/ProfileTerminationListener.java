package edument.perl6idea.profiler;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Iterator;
import java.util.stream.Stream;

public class ProfileTerminationListener extends ProcessAdapter {
    public static Logger LOG = Logger.getInstance(ProfileTerminationListener.class);
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
        ProgressManager.getInstance().run(new Task.Modal(project, "Processing Profiling Data", false) {
            public void run(@NotNull ProgressIndicator indicator) {
                Connection connection = null;
                try {
                    // Create in-memory DB
                    indicator.setText("Creating database...");
                    indicator.setFraction(0.1);
                    connection = DriverManager.getConnection("jdbc:sqlite::memory:");
                    Statement statement = connection.createStatement();
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
                }
                catch (SQLException | IOException e) {
                    LOG.warn(e);
                }
                finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        }
                        catch (SQLException e) {
                            LOG.warn(e);
                        }
                    }
                    file.delete();
                }
            }
        });
    }
}
