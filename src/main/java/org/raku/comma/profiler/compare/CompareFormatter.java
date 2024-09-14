package org.raku.comma.profiler.compare;

import java.util.List;

public abstract class CompareFormatter {
    public abstract Object[][] format(List<ProfileCompareProcessor.ProfileCompareRow> rows, List<ProfileCompareProcessor.ProfileCompareColumn> columns);

    public abstract Object[] formatTableColumns(List<ProfileCompareProcessor.ProfileCompareColumn> columns);

    protected static String diff(ProfileCompareProcessor.ProfileMetricValue data) {
        return diff(data.first, data.second);
    }

    protected static String diff(int x, int y) {
        if (x == y)
            return "-";
        if (x == 0)
            return "(+∞%)";
        int diff = y - x;
        float percent = 100 * ((float)y / x) - 100;
        return String.format("%,d (%s%.2f%%)", diff, percent > 0 ? "+" : "", percent);
    }

    protected static String orAnon(String s) {
        if (s == null || s.isEmpty()) return "<anon>";
        return s;
    }
}
