package edument.perl6idea.profiler.model;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.Function;
import org.jetbrains.annotations.Nullable;

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
    public static final Function<String, String> RELATED_CALL_NODES_SQL = (table) ->
        "SELECT rs.id, rs.name, rs.file, rs.line," +
        "total(case when c.rec_depth = 0 then c.inclusive_time else 0 end) as inclusive_time," +
        "c.exclusive_time, c.entries " +
        table +
        "JOIN routines rs ON c.routine_id == rs.id " +
        "WHERE pc.routine_id = ? " +
        "GROUP BY c.routine_id " +
        "ORDER BY c.inclusive_time DESC;";
    private Connection connection;
    private final String sqlDataFilePath;

    public Perl6ProfileData(String pathToSqlFile) throws SQLException {
        sqlDataFilePath = pathToSqlFile;
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    }

    public void initialize() throws IOException, SQLException {
        try (Statement statement = connection.createStatement()) {
            Stream<String> lines = Files.lines(Paths.get(sqlDataFilePath), StandardCharsets.UTF_8);
            Iterator<String> iterator = lines.iterator();
            // Load the base from the file
            while (iterator.hasNext()) {
                statement.executeUpdate(iterator.next());
            }
        }
    }

    public void cancel() {
        // A race?
        if (connection == null)
            return;

        try {
            connection.close();
            connection = null;
        }
        catch (SQLException e) {
            LOG.warn(e);
            connection = null;
        }
    }

    public Perl6ProfileCall getProfileCallById(int callId, int max, @Nullable Perl6ProfileCall parent) {
        String sql = "SELECT c.id AS id, c.inclusive_time AS inclusive, " +
                     "r.name AS name " +
                     "FROM calls c JOIN routines r ON c.routine_id = r.id " +
                     "WHERE (c.parent_id = ? and c.inclusive_time > 1) or c.id = ? " +
                     "ORDER BY c.id ASC;";
        Perl6ProfileCall root = new Perl6ProfileCall();
        if (parent != null)
            root.parent = parent;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, callId);
            statement.setInt(2, callId);
            ResultSet set = statement.executeQuery();
            // For every callee
            List<Perl6ProfileCall> calleeList = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("id");
                int time = set.getInt("inclusive");
                String name = set.getString("name");
                if (id == callId) {
                    root.name = name;
                    root.time = time;
                } else if (max > 0) {
                    calleeList.add(getProfileCallById(id, max - 1, root));
                }
            }
            root.callees = calleeList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return root;
    }

    public List<Perl6ProfilerNode> getNavigationNodes() throws SQLException {
        List<Perl6ProfilerNode> nodes = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet calls = statement
                .executeQuery("SELECT r.id, r.file, r.line, r.name, SUM(c.inclusive_time) as inclusive_time, " +
                              "SUM(c.exclusive_time) as exclusive_time, SUM(c.entries) as entries " +
                              "FROM calls c INNER JOIN routines r ON c.routine_id == r.id " +
                              "GROUP BY r.id ORDER BY c.inclusive_time DESC");
            convertProfilerNodes(nodes, calls);
            // XXX remove(0) has complexity O(n) for an ArrayList we use here,
            // but it's not clear if we should to make `convertProfilerNodes` more specific
            // by adding a check "Is it a first element? Then skip it" for every node iteration.
            // And we are likely to get exact elements using an index quite often, so LinkedList isn't usable here
            nodes.remove(0);
        }
        return nodes;
    }

    private static void convertProfilerNodes(List<Perl6ProfilerNode> nodes, ResultSet calls) throws SQLException {
        while (calls.next()) {
            nodes.add(new Perl6ProfilerNode(
                calls.getInt("id"),
                calls.getString("file"),
                calls.getInt("line"),
                calls.getString("name"),
                calls.getInt("inclusive_time"),
                calls.getInt("exclusive_time"),
                calls.getInt("entries")
            ));
        }
    }

    private List<Perl6ProfilerNode> getRelatedCallNodes(int id, boolean wantCallers) {
        try {
            if (!connection.isClosed()) {
                List<Perl6ProfilerNode> calleeList = new ArrayList<>();
                String sql = RELATED_CALL_NODES_SQL.fun(
                    wantCallers ?
                    "FROM calls pc JOIN calls c ON c.id == pc.parent_id " :
                    "FROM calls c JOIN calls pc ON pc.id == c.parent_id ");
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, id);
                    ResultSet callees = statement.executeQuery();
                    convertProfilerNodes(calleeList, callees);
                }
                return calleeList;
            }
        }
        catch (SQLException e) {
            LOG.warn(e);
        }
        return new ArrayList<>();
    }

    public List<Perl6ProfilerNode> getCalleeListByCallId(int id) {
        return getRelatedCallNodes(id,false);
    }

    public List<Perl6ProfilerNode> getCallerListByCallId(int id) {
        return getRelatedCallNodes(id, true);
    }
}
