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

public class ProfileCompareGCs extends ProfileCompareDataProvider {
    private final static String sql;

    static {
        sql = Perl6Utils.getResourceAsString("sql/compare-profiler-gcs-results.sql");
    }

    private final Statement stmt;

    ProfileCompareGCs(Statement stmt) {
        this.stmt = stmt;
    }

    public void addTabs(ProfileCompareProcessor.ProfileCompareResults results) {
        results.addTab(new ProfileCompareTab("GC", TAB_GC, this, SingleRowCompareFormatter.INSTANCE));
    }

    public static final List<ProfileCompareProcessor.ProfileCompareColumn> TAB_GC = ContainerUtil.immutableList(
        new ProfileCompareProcessor.ProfileCompareColumn("Total Time", "total_time"),
        new ProfileCompareProcessor.ProfileCompareColumn("Avg Time", "avg_time"),
        new ProfileCompareProcessor.ProfileCompareColumn("Avg Retained Bytes", "retained_bytes"),
        new ProfileCompareProcessor.ProfileCompareColumn("Avg Promoted Bytes", "promoted_bytes")
    );

    private static String[] METRICS = new String[]{
        "total_time",
        "avg_time",
        "retained_bytes",
        "promoted_bytes"
    };

    @Override
    protected List<ProfileCompareProcessor.ProfileCompareRow> getRows() throws SQLException {
        ResultSet rs = stmt.executeQuery(sql);
        ArrayList<ProfileCompareProcessor.ProfileCompareRow> results = new ArrayList<>();

        while (rs.next()) {
            HashMap<String, ProfileCompareProcessor.ProfileMetricValue> metrics = new HashMap<>();
            for (String metric : METRICS) {
                metrics.put(metric, new ProfileCompareProcessor.ProfileMetricValue(rs.getInt(metric)));
            }
            results.add(new ProfileCompareProcessor.ProfileCompareRow("", "", metrics));
        }

        rs.close();
        return results;
    }
}
