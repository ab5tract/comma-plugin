package edument.perl6idea.profiler.compare;

import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ProfileCompareCalls extends ProfileCompareDataProvider {
    private final static String sql;

    static {
        sql = Perl6Utils.getResourceAsString("sql/compare-profiler-calls-results.sql");
    }

    private final Statement stmt;

    ProfileCompareCalls(Statement stmt) {
        this.stmt = stmt;
    }

    public void addTabs(ProfileCompareProcessor.ProfileCompareResults results) {
        results.addTab(new ProfileCompareTab("Entries", TAB_ENTRIES, this, ByNameCompareFormatter.BOTH_NAMES));
        results.addTab(new ProfileCompareTab("Time", TAB_TIME, this, ByNameCompareFormatter.BOTH_NAMES));
        results.addTab(new ProfileCompareTab("Deopt", TAB_DEOPT, this, ByNameCompareFormatter.BOTH_NAMES));
    }

    public static final List<ProfileCompareProcessor.ProfileCompareColumn> TAB_ENTRIES = ContainerUtil.immutableList(
        new ProfileCompareProcessor.ProfileCompareColumn("JIT entries", "jit_entries"),
        new ProfileCompareProcessor.ProfileCompareColumn("Inlined entries", "inlined_entries"),
        new ProfileCompareProcessor.ProfileCompareColumn("Spesh entries", "spesh_entries")
    );

    public static final List<ProfileCompareProcessor.ProfileCompareColumn> TAB_TIME = ContainerUtil.immutableList(
        new ProfileCompareProcessor.ProfileCompareColumn("Total calls", "entries"),
        new ProfileCompareProcessor.ProfileCompareColumn("Inclusive time", "inclusive_time"),
        new ProfileCompareProcessor.ProfileCompareColumn("Exclusive time", "exclusive_time")
    );

    public static final List<ProfileCompareProcessor.ProfileCompareColumn> TAB_DEOPT = ContainerUtil.immutableList(
        new ProfileCompareProcessor.ProfileCompareColumn("Deopt: One", "deopt_one"),
        new ProfileCompareProcessor.ProfileCompareColumn("Deopt: All", "deopt_all")
    );

    private static final String[] METRICS = new String[]{
        "spesh_entries",
        "jit_entries",
        "inlined_entries",
        "inclusive_time",
        "exclusive_time",
        "entries",
        "deopt_one",
        "deopt_all"
    };

    @Override
    @NotNull
    protected List<ProfileCompareProcessor.ProfileCompareRow> getRows() throws SQLException {
        Map<Integer, Integer> db1Inclusives = computeInclusives(stmt, "db1.calls");
        Map<Integer, Integer> db2Inclusives = computeInclusives(stmt, "db2.calls");

        ResultSet rs = stmt.executeQuery(sql);
        ArrayList<ProfileCompareProcessor.ProfileCompareRow> results = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String lftName = rs.getString("r1_name");
            String rgtName = rs.getString("r2_name");
            HashMap<String, ProfileCompareProcessor.ProfileMetricValue> metrics = new HashMap<>();
            for (String metric : METRICS) {
                metrics.put(metric, new ProfileCompareProcessor.ProfileMetricValue(rs.getInt("c1_" + metric), rs.getInt("c2_" + metric)));
            }
            metrics.put("inclusive_time", new ProfileCompareProcessor.ProfileMetricValue(db1Inclusives.getOrDefault(id, 0), db2Inclusives.getOrDefault(id, 0)));

            results.add(new ProfileCompareProcessor.ProfileCompareRow(lftName, rgtName, metrics));
        }

        rs.close();
        return results;
    }

    @NotNull
    private static Map<Integer, Integer> computeInclusives(Statement stmt, String tableName) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT id, routine_id, parent_id, inclusive_time FROM " + tableName + " GROUP BY 1"); // TODO why group by?

        HashMap<Integer, Integer> parentOf = new HashMap<>();
        HashMap<Integer, Integer> toRoutine = new HashMap<>();
        HashSet<Integer> routineIds = new HashSet<>();
        HashMap<Integer, Integer> inclusiveTimes = new HashMap<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            int routineId = rs.getInt("routine_id");
            int parentId = rs.getInt("parent_id");

            inclusiveTimes.put(id, rs.getInt("inclusive_time"));
            routineIds.add(routineId);
            toRoutine.put(id, routineId);

            if (id != parentId) {
                parentOf.put(id, parentId);
            }
        }

        HashMap<Integer, Integer> results = new HashMap<>();
        for (int id : routineIds) {
            int inclusive = 0;
            for (Map.Entry<Integer, Integer> entry : toRoutine.entrySet()) {
                if (entry.getValue().equals(id)) {
                  inclusive += calculateInclusive(parentOf, inclusiveTimes, entry.getKey());
                }
            }
            results.put(id, inclusive);
        }
        return results;
    }

    private static int calculateInclusive(HashMap<Integer, Integer> parentOf, HashMap<Integer, Integer> inclusiveTimes, int id) {
        int inclusiveTime = 0;
        while (true) {
            inclusiveTime += inclusiveTimes.get(id);

            int parent = parentOf.getOrDefault(id, -1);
            if (parent == -1) {
               return inclusiveTime;
            }
            id = parent;
        }
    }
}
