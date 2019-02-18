package edument.perl6idea.profiler;

import com.intellij.openapi.diagnostic.Logger;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Perl6ProfileData {
    public static final Logger LOG = Logger.getInstance(Perl6ProfileData.class);
    private final Connection connection;
    private final String sqlDataFilePath;

    public Perl6ProfileData(String pathToSqlFile) throws SQLException {
        sqlDataFilePath = pathToSqlFile;
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    }

    public void initialize() throws IOException, SQLException {
        Statement statement = connection.createStatement();
        Stream<String> lines = Files.lines(Paths.get(sqlDataFilePath), StandardCharsets.UTF_8);
        Iterator<String> iterator = lines.iterator();
        // Load the base from the file
        while (iterator.hasNext()) {
            statement.executeUpdate(iterator.next());
        }
        statement.close();
    }

    public List<Perl6ProfilerNode> getNavigationNodes() throws SQLException {
        List<Perl6ProfilerNode> nodes = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet calls = statement
            .executeQuery("SELECT r.file, r.line, r.name, c.inclusive_time, c.exclusive_time, c.entries, json_group_array(sr.name) as callee " +
                          "FROM calls c INNER JOIN calls sc ON sc.parent_id == c.id INNER JOIN routines sr " +
                          "ON sc.routine_id == sr.id INNER JOIN routines r ON c.routine_id == r.id " +
                          "GROUP BY c.id ORDER BY c.inclusive_time DESC");

        while (calls.next()) {
            JSONArray calleeJSON = new JSONArray(calls.getString("callee"));
            List<CalleeNode> callees = new ArrayList<>();
            for (int i = 0; i < calleeJSON.length(); i++) {
                callees.add(new CalleeNode(calleeJSON.getString(i)));
            }
            nodes.add(new Perl6ProfilerNode(
                calls.getString("file"),
                calls.getInt("line"),
                calls.getString("name"),
                calls.getInt("inclusive_time"),
                calls.getInt("exclusive_time"),
                calls.getInt("entries"),
                callees
            ));
        }
        statement.close();
        return nodes;
    }

    public void cancel() {
        // A race?
        if (connection == null)
            return;

        try {
            connection.close();
        }
        catch (SQLException e) {
            LOG.warn(e);
        }
    }
}
