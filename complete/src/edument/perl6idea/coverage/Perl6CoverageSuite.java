package edument.perl6idea.coverage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Perl6CoverageSuite {
    private final String name;
    private final String timestamp;
    private final String path;

    // The outermost level is the key of the data (when tests, the test file). The
    // inner one then maps file absolute paths into the set of covered lines.
    private final Map<String, Map<String, Set<Integer>>> data = new HashMap<>();

    public Perl6CoverageSuite(String name, String timestamp, String path) {
        this.name = name;
        this.timestamp = timestamp;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void addCoverageData(String name, Map<String, Set<Integer>> covered) {
        data.put(name, covered);
    }

    // Finds all data for the path in question. Returns a hash mapping the data key
    // (in the case of tests, the test name) into covered line numbers.
    public Map<String, Set<Integer>> lineDataForPath(String path) {
        Map<String, Set<Integer>> pathData = new HashMap<>();
        for (String key : data.keySet()) {
            Set<Integer> lineData = data.get(key).get(path);
            if (lineData != null)
                pathData.put(key, lineData);
        }
        return pathData;
    }
}
