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
        String sql = "SELECT c.id AS id, c.inclusive_time AS inclusive, c.entries as entries, " +
                     "c.spesh_entries AS spesh_entries, c.inlined_entries AS inlined_entries, " +
                     "r.name AS name, r.file AS file, r.line AS line " +
                     "FROM calls c JOIN routines r ON c.routine_id = r.id " +
                     "WHERE (c.parent_id = ? and c.inclusive_time > 1) or c.id = ? " +
                     "ORDER BY c.id ASC;";
        Perl6ProfileCall root = new Perl6ProfileCall();
        root.id = callId;
        if (parent != null)
            root.parent = parent;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, callId);
            statement.setInt(2, callId);
            ResultSet set = statement.executeQuery();
            // For every callee
            List<Perl6ProfileCall> calleeList = new ArrayList<>();
            boolean hasMoreChildren = false;
            while (set.next()) {
                int id = set.getInt("id");
                if (id == callId) {
                    root.name = set.getString("name");
                    root.filename = set.getString("file");
                    root.line = set.getString("line");
                    root.inclusiveTime = set.getInt("inclusive");
                    root.entries = set.getInt("entries");
                    root.inlinedEntries = set.getInt("inlined_entries");
                    root.speshEntries = set.getInt("spesh_entries");
                } else {
                    hasMoreChildren = true;
                    if (max > 0)
                        calleeList.add(getProfileCallById(id, max - 1, root));
                }
            }
            if (max == 0 && hasMoreChildren)
                root.callees = null;
            else
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
                .executeQuery("SELECT r.id, r.file, r.line, r.name, " +
                              "total(case when rec_depth = 0 then inclusive_time else 0 end) as inclusive_time, " +
                              "total(exclusive_time) as exclusive_time, " +
                              "SUM(c.entries) as entries " +
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

    public List<Perl6ProfileThread> getThreads() {
        List<Perl6ProfileThread> threadList = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet threads = statement
                .executeQuery("SELECT thread_id AS id, root_node AS rootNodeID FROM profile WHERE rootNodeID IS NOT NULL ORDER BY id ASC;");
            while (threads.next()) {
                threadList.add(new Perl6ProfileThread(threads.getInt("id"), threads.getInt("rootNodeID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return threadList;
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
