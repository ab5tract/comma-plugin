package edument.perl6idea.profiler;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.Function;
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
    public static final Function<String, String> RELATED_CALL_NODES_SQL = (table) ->
        "SELECT c.id, rs.name, rs.file, rs.line," +
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
            .executeQuery("SELECT r.id, r.file, r.line, r.name, c.inclusive_time, c.exclusive_time, c.entries, json_group_array(sr.name) as callee " +
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
                calls.getInt("id"),
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
            connection = null;
        }
        catch (SQLException e) {
            LOG.warn(e);
            connection = null;
        }
    }

    public List<Perl6ProfilerNode> getCalleeListByCallId(int id) {
        try {
            if (!connection.isClosed()) {
                List<Perl6ProfilerNode> calleeList = new ArrayList<>();
                PreparedStatement statement = connection.prepareStatement(
                    RELATED_CALL_NODES_SQL.fun("FROM calls c JOIN calls pc ON pc.id == c.parent_id "));
                statement.setInt(1, id);
                ResultSet callees = statement.executeQuery();
                while (callees.next()) {
                    calleeList.add(new Perl6ProfilerNode(
                        callees.getInt("id"),
                        callees.getString("file"),
                        callees.getInt("line"),
                        callees.getString("name"),
                        callees.getInt("inclusive_time"),
                        callees.getInt("exclusive_time"),
                        callees.getInt("entries"),
                        new ArrayList<>()
                    ));
                }
                statement.close();
                return calleeList;
            }
        }
        catch (SQLException e) {
            LOG.warn(e);
        }
        return new ArrayList<>();
    }

    public List<Perl6ProfilerNode> getCallerListByCallId(int id) {
        try {
            if (!connection.isClosed()) {
                List<Perl6ProfilerNode> calleeList = new ArrayList<>();
                PreparedStatement statement = connection.prepareStatement(
                    RELATED_CALL_NODES_SQL.fun("FROM calls pc JOIN calls c ON c.id == pc.parent_id ")
                );
                statement.setInt(1, id);
                ResultSet callees = statement.executeQuery();
                while (callees.next()) {
                    calleeList.add(new Perl6ProfilerNode(
                        callees.getInt("id"),
                        callees.getString("file"),
                        callees.getInt("line"),
                        callees.getString("name"),
                        callees.getInt("inclusive_time"),
                        callees.getInt("exclusive_time"),
                        callees.getInt("entries"),
                        new ArrayList<>()
                    ));
                }
                statement.close();
                return calleeList;
            }
        }
        catch (SQLException e) {
            LOG.warn(e);
        }
        return new ArrayList<>();
    }
}
