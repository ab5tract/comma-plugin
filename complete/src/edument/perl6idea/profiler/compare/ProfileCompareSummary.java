package edument.perl6idea.profiler.compare;

import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.utils.Perl6Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileCompareSummary extends ProfileCompareDataProvider {
    private final static String sql;

    static {
        sql = Perl6Utils.getResourceAsString("sql/compare-profiler-summary-results.sql");
    }

    private final Statement stmt;

    public ProfileCompareSummary(Statement stmt) {
        this.stmt = stmt;
    }


    @Override
    protected List<ProfileCompareProcessor.ProfileCompareRow> getRows() throws SQLException {
        ArrayList<ProfileCompareProcessor.ProfileCompareRow> results = new ArrayList<>();
        results.add(summarizeDB("db1"));
        results.add(summarizeDB("db2"));
        return results;
    }

    private ProfileCompareProcessor.ProfileCompareRow summarizeDB(String dbName) throws SQLException {
        ResultSet rs = stmt.executeQuery(sql.replaceAll("%DB%", dbName));

        rs.next();
        HashMap<String, ProfileCompareProcessor.ProfileMetricValue> metrics = new HashMap<>();
        for (String metric : METRICS) {
            metrics.put(metric, new ProfileCompareProcessor.ProfileMetricValue(rs.getInt(metric), 0));
        }
        for (String metric : METRICS_MS) {
            metrics.put(metric, new ProfileCompareProcessor.ProfileMetricValue(rs.getInt(metric), 0, "ms"));
        }
        rs.close();

        return new ProfileCompareProcessor.ProfileCompareRow("", "", metrics);
    }

    public static final String[] METRICS = new String[] {
        "gc_count",
        "gc_full_count",
        "calls_entries",
        "calls_spesh_entries",
        "calls_jit_entries",
        "calls_inlined_entries",
        "calls_deopt_all",
        "calls_deopt_one",
        "allocations_total",
        "allocations_replaced"
    };

    public static final String[] METRICS_MS = new String[] {
        "gc_total_time",
        "gc_avg_minor_time",
        "gc_min_minor_time",
        "gc_max_minor_time",
        "gc_avg_major_time",
        "gc_min_major_time",
        "gc_max_major_time",
        "profile_total_time",
        "profile_total_spesh_time",
        "profile_highest_spesh_time",
        "profile_run_time"
    };

    public static final List<Pair<String, String>> TAB_SUMMARY = ContainerUtil.immutableList(
        Pair.create("GC run", "gc_count"),
        Pair.create("GC major count", "gc_full_count"),
        Pair.create("GC total time", "gc_total_time"),
        Pair.create("GC avg minor time", "gc_avg_minor_time"),
        Pair.create("GC min minor time", "gc_min_minor_time"),
        Pair.create("GC max minor time", "gc_max_minor_time"),
        Pair.create("GC avg major time", "gc_avg_major_time"),
        Pair.create("GC min major time", "gc_min_major_time"),
        Pair.create("GC max major time", "gc_max_major_time"),
        Pair.create("Total time", "profile_total_time"),
        Pair.create("Total spesh time", "profile_total_spesh_time"),
        Pair.create("Highest spesh time", "profile_highest_spesh_time"),
        Pair.create("Run Total Time", "profile_run_time"),
        Pair.create("Entries", "calls_entries"),
        Pair.create("Spesh Entries", "calls_spesh_entries"),
        Pair.create("JIT Entries", "calls_jit_entries"),
        Pair.create("Inlined Entries", "calls_inlined_entries"),
        Pair.create("Deopt(All)", "calls_deopt_all"),
        Pair.create("Deopt(1)", "calls_deopt_one"),
        Pair.create("Allocations total", "allocations_total"),
        Pair.create("Allocations replaced", "allocations_replaced")
    );

    public void addTabs(ProfileCompareProcessor.ProfileCompareResults results) {
        results.addTab(new ProfileCompareTab("Summary", TAB_SUMMARY, this, SingleRowCompareFormatter.INSTANCE));
    }
}
