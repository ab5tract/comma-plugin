package org.raku.comma.profiler.compare;

import com.intellij.util.containers.ContainerUtil;

import java.util.List;

public class ByNameCompareFormatter extends CompareFormatter {
    public static final ByNameCompareFormatter SINGLE_NAME = new ByNameCompareFormatter(false);
    public static final ByNameCompareFormatter BOTH_NAMES = new ByNameCompareFormatter(true);
    private final boolean bothNames;

    private ByNameCompareFormatter(boolean bothNames) {
        this.bothNames = bothNames;
    }

    @Override
    public Object[][] format(List<ProfileCompareProcessor.ProfileCompareRow> rows, List<ProfileCompareProcessor.ProfileCompareColumn> columns) {
        int offset = getOffset();
        Object[][] allData = new Object[rows.size()][offset + 3 * columns.size()];

        // Add a row for every column for every row (C*R)
        for (int i = 0; i < rows.size(); i++) {
            ProfileCompareProcessor.ProfileCompareRow result = rows.get(i);

            // Only display both names if they might be different
            allData[i][0] = orAnon(result.myLftName);
            if (bothNames) {
                allData[i][1] = orAnon(result.myRgtName);
            }

            for (int j = 0; j < columns.size(); j++) {
                ProfileCompareProcessor.ProfileCompareColumn column = columns.get(j);
                ProfileCompareProcessor.ProfileMetricValue metricData = result.myMetrics.get(column.key);
                allData[i][offset + j * 3] = column.format(metricData.first);
                allData[i][offset + 1 + j * 3] = column.format(metricData.second);
                allData[i][offset + 2 + j * 3] = diff(metricData);
            }
        }

        return allData;
    }

    // Skip rows for which all the (used) values are 0 on both sides
    private static boolean isRowEmpty(ProfileCompareProcessor.ProfileCompareRow result, List<ProfileCompareProcessor.ProfileCompareColumn> columns) {
        for (ProfileCompareProcessor.ProfileCompareColumn column : columns) {
            ProfileCompareProcessor.ProfileMetricValue metricData = result.myMetrics.get(column.key);
            if (metricData.first != 0 || metricData.second != 0)
                return false;
        }
        return true;
    }

    private int getOffset() {
        return bothNames ? 2 : 1;
    }

    @Override
    public Object[] formatTableColumns(List<ProfileCompareProcessor.ProfileCompareColumn> columns) {
        int offset = getOffset();
        Object[] columnNames = ContainerUtil.map2Array(columns, p -> p.name);
        String[] objects = new String[offset + 3 * columnNames.length];

        // Only display both names if they might differ
        if (bothNames) {
            objects[0] = "Name (A)";
            objects[1] = "Name (B)";
        } else {
            objects[0] = "Name";
        }

        for (int i = 0; i < columnNames.length; i++) {
            String col = (String)columnNames[i];
            objects[offset + 3 * i] = col + " (A)";
            objects[offset + 1 + 3 * i] = col + " (B)";
            objects[offset + 2 + 3 * i] = col + " (%)";
        }
        return objects;
    }
}
