package edument.perl6idea.profiler.compare;

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
    private final String leftName;
    private final String rightName;

    public ProfileCompareSummary(Statement stmt, String leftName, String rightName) {
        this.stmt = stmt;
        this.leftName = leftName;
        this.rightName = rightName;
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
        "allocations_replaced",
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

    public static final List<ProfileCompareProcessor.ProfileCompareColumn> TAB_SUMMARY = ContainerUtil.immutableList(
        new ProfileCompareProcessor.ProfileCompareColumn("GC run", "gc_count"),
        new ProfileCompareProcessor.ProfileCompareColumn("GC major count", "gc_full_count"),
        new ProfileCompareProcessor.ProfileCompareColumn("GC total time", "gc_total_time", "ms"),
        new ProfileCompareProcessor.ProfileCompareColumn("Nursery avg time", "gc_avg_minor_time", "ms"),
        new ProfileCompareProcessor.ProfileCompareColumn("Nursery min time", "gc_min_minor_time", "ms"),
        new ProfileCompareProcessor.ProfileCompareColumn("Nursery max time", "gc_max_minor_time", "ms"),
        new ProfileCompareProcessor.ProfileCompareColumn("Full avg time", "gc_avg_major_time", "ms"),
        new ProfileCompareProcessor.ProfileCompareColumn("Full min time", "gc_min_major_time", "ms"),
        new ProfileCompareProcessor.ProfileCompareColumn("Full max time", "gc_max_major_time", "ms"),
        new ProfileCompareProcessor.ProfileCompareColumn("Total time", "profile_total_time", "ms"),
        new ProfileCompareProcessor.ProfileCompareColumn("Total spesh time", "profile_total_spesh_time", "ms"),
        new ProfileCompareProcessor.ProfileCompareColumn("Highest spesh time", "profile_highest_spesh_time", "ms"),
        new ProfileCompareProcessor.ProfileCompareColumn("Run Total Time", "profile_run_time", "ms"),
        new ProfileCompareProcessor.ProfileCompareColumn("Entries", "calls_entries"),
        new ProfileCompareProcessor.ProfileCompareColumn("Spesh", "calls_spesh_entries"),
        new ProfileCompareProcessor.ProfileCompareColumn("JIT", "calls_jit_entries"),
        new ProfileCompareProcessor.ProfileCompareColumn("Inlined", "calls_inlined_entries"),
        new ProfileCompareProcessor.ProfileCompareColumn("Deopt(All)", "calls_deopt_all"),
        new ProfileCompareProcessor.ProfileCompareColumn("Deopt(1)", "calls_deopt_one"),
        new ProfileCompareProcessor.ProfileCompareColumn("Allocations total", "allocations_total"),
        new ProfileCompareProcessor.ProfileCompareColumn("Allocations replaced", "allocations_replaced")
    );

    public void addTabs(ProfileCompareProcessor.ProfileCompareResults results) {
        results.addTab(new ProfileCompareTab("Summary", TAB_SUMMARY, this, new SingleRowCompareFormatter(this.leftName, this.rightName)));
    }
}
