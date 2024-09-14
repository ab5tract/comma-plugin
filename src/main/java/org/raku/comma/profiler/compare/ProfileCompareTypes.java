package org.raku.comma.profiler.compare;

import org.raku.comma.utils.RakuUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static kotlinx.collections.immutable.ExtensionsKt.persistentListOf;

public class ProfileCompareTypes extends ProfileCompareDataProvider {
    private final static String sql;

    static {
        sql = RakuUtils.getResourceAsString("sql/compare-profiler-types-results.sql");
    }

    private final Statement stmt;

    public ProfileCompareTypes(Statement stmt) {
        this.stmt = stmt;
    }

    public void addTabs(ProfileCompareProcessor.ProfileCompareResults results) {
        results.addTab(new ProfileCompareTab("Allocations", TAB_ALLOCS, this, ByNameCompareFormatter.SINGLE_NAME));
        results.addTab(new ProfileCompareTab("Deallocations", TAB_DEALLOCS, this, ByNameCompareFormatter.SINGLE_NAME));
    }

    private static final String[] METRICS = new String[]{
        "spesh",
        "jit",
        "count",
        "replaced",
        "nursery_fresh",
        "nursery_seen",
        "gen2",
        "num_gcs"
    };

    // ???
    public static final List<ProfileCompareProcessor.ProfileCompareColumn> TAB_ALLOCS = persistentListOf(
        //Pair.create("Optimized by Spesh", "spesh"),
        //Pair.create("JIT", "jit"),
        new ProfileCompareProcessor.ProfileCompareColumn("Count", "count"),
        new ProfileCompareProcessor.ProfileCompareColumn("Optimized Away", "replaced")
    );

    public static final List<ProfileCompareProcessor.ProfileCompareColumn> TAB_DEALLOCS = persistentListOf(
        new ProfileCompareProcessor.ProfileCompareColumn("Nursery: Fresh", "nursery_fresh"),
        new ProfileCompareProcessor.ProfileCompareColumn("Nursery: Seen", "nursery_seen"),
        new ProfileCompareProcessor.ProfileCompareColumn("Reached old generation", "gen2"),
        new ProfileCompareProcessor.ProfileCompareColumn("GC cycles with that type", "num_gcs")
    );

    @Override
    @NotNull
    public List<ProfileCompareProcessor.ProfileCompareRow> getRows() throws SQLException {
        ResultSet rs = stmt.executeQuery(sql);
        ArrayList<ProfileCompareProcessor.ProfileCompareRow> results = new ArrayList<>();
        while (rs.next()) {
            HashMap<String, ProfileCompareProcessor.ProfileMetricValue> metrics = new HashMap<>();
            for (String metric : METRICS) {
                metrics.put(metric, new ProfileCompareProcessor.ProfileMetricValue(rs.getInt("c1_" + metric), rs.getInt("c2_" + metric)));
            }

          results.add(new ProfileCompareProcessor.ProfileCompareRow(rs.getString("name"), "", metrics));
        }
        rs.close();
        return results;
    }
}
