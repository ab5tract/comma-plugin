package edument.perl6idea.profiler.compare;

import edument.perl6idea.profiler.model.Perl6ProfileData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileCompareProcessor {
    private final Perl6ProfileData[] myData;
    private String myDbPath;

    public ProfileCompareProcessor(Perl6ProfileData[] data) {
        myData = data;
    }

    static public class ProfileCompareResults {
        private List<ProfileCompareTab> tabs = new ArrayList<>();

        public void addTab(ProfileCompareTab tab) {
            tabs.add(tab);
        }

        public List<ProfileCompareTab> getTabs() {
            return tabs;
        }
    }

    static public class ProfileCompareColumn {
        String name;
        String key;
        String unit;

        public ProfileCompareColumn(String name, String key, String unit) {
            this.name = name;
            this.key = key;
            this.unit = unit;
        }

        public ProfileCompareColumn(String name, String key) {
            this.name = name;
            this.key = key;
            this.unit = null;
        }

        public String format(int v) {
            if (unit == null)
                return String.format("%,d", v);//.trim();
            return String.format("%,d %s", v, unit);//.trim();
        }
    }

    static public class ProfileMetricValue {
        public final int first;
        public final int second;

        ProfileMetricValue(int first) {
            this.first = first;
            this.second = first;
        }

        ProfileMetricValue(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }

    static public class ProfileCompareRow {
        public final String myLftName;
        public final String myRgtName;
        public final Map<String, ProfileMetricValue> myMetrics;

        public ProfileCompareRow(String lftName,
                                 String rgtName,
                                 Map<String, ProfileMetricValue> metrics) {
            myLftName = lftName;
            myRgtName = rgtName;
            myMetrics = metrics;
        }
    }

    public ProfileCompareResults process() throws SQLException, IOException {
        Connection connection = connect();
        myData[0].initialize();
        myData[1].initialize();

        ProfileCompareResults results;

        try {
            Statement stmt = connection.createStatement();
            stmt.execute("ATTACH DATABASE '" + myData[0].getFileName() + "' as db1");
            stmt.execute("ATTACH DATABASE '" + myData[1].getFileName() + "' as db2");
            stmt.close();

            results = new ProfileCompareResults();
            ProfileCompareSummary compareSummary = new ProfileCompareSummary(connection.createStatement(), myData[0].getName(), myData[1].getName());
            ProfileCompareCalls compareCalls = new ProfileCompareCalls(connection.createStatement());
            ProfileCompareTypes compareTypes = new ProfileCompareTypes(connection.createStatement());
            ProfileCompareGCs compareGCs = new ProfileCompareGCs(connection.createStatement(), myData[0].getName(), myData[1].getName());

            compareSummary.addTabs(results);
            compareCalls.addTabs(results);
            compareTypes.addTabs(results);
            compareGCs.addTabs(results);
        } finally {
            // Need to compose some kind of callback from all the comparators
            //connection.close();
        }

        return results;
    }

    private Connection connect() throws IOException, SQLException {
        try {
            Path filePath = Files.createTempFile("comma-profile-tmp", ".sqlite3");
            myDbPath = filePath.toString();
            return DriverManager.getConnection("jdbc:sqlite:" + filePath);
        } catch (IOException ex) {
            throw new IOException("Could not create a temporary database: " + ex.getMessage());
        }
    }
}
