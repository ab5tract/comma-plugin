package edument.perl6idea.profiler;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.diagnostic.Logger;
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

    ProfileTerminationListener(File file) {
        this.file = file;
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        if (event.getExitCode() != 0) {
            file.delete();
            return;
        }

        Connection connection = null;
        try {
            // Create in-memory DB
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            Statement statement = connection.createStatement();
            Stream<String> lines = Files.lines(Paths.get(file.getCanonicalPath()), StandardCharsets.UTF_8);
            Iterator<String> iterator = lines.iterator();
            // Load base
            while (iterator.hasNext())
                statement.executeUpdate(iterator.next());
        }
        catch (SQLException|IOException e) {
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
}
