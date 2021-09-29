package edument.perl6idea.profiler.compare;

import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;

import java.util.List;

public abstract class CompareFormatter {
    public abstract Object[][] format(List<ProfileCompareProcessor.ProfileCompareRow> rows, List<Pair<String, String>> columns);

    public Object[] getColumnNames(List<Pair<String, String>> columns) {
      return ContainerUtil.map2Array(columns, p -> p.first);
    }

    public abstract Object[] formatTableColumns(List<Pair<String, String>> columns);


    protected static String diff(ProfileCompareProcessor.ProfileMetricValue data) {
        return diff(data.first, data.second);
    }

    protected static String diff(int x, int y) {
        if (x == y)
            return "-";
        if (x == 0)
            return "(+∞%)";
        int diff = y - x;
        int percent = 100 * (y / x) - 100;
        return diff + " (" + (percent > 0 ? "+" : "") + percent + "%)";
    }

    protected static String orAnon(String s) {
        if (s == null || s.isEmpty()) return "<anon>";
        return s;
    }
}
