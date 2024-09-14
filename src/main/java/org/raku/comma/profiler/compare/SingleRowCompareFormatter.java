package org.raku.comma.profiler.compare;

import java.util.List;

public class SingleRowCompareFormatter extends CompareFormatter {
    private final String[] TABLE_COLUMNS;

    public SingleRowCompareFormatter(String nameLeft, String nameRight) {
        TABLE_COLUMNS = new String[] {
            "Metric", nameLeft, nameRight, "%"
        };
    }

    @Override
    public Object[][] format(List<ProfileCompareProcessor.ProfileCompareRow> rows, List<ProfileCompareProcessor.ProfileCompareColumn> columns) {
        Object[][] objects = new Object[columns.size()][4];
        ProfileCompareProcessor.ProfileCompareRow fst = rows.get(0);
        ProfileCompareProcessor.ProfileCompareRow snd = rows.get(1);
        for (int i = 0; i < columns.size(); i++) {
            ProfileCompareProcessor.ProfileCompareColumn column = columns.get(i);
            ProfileCompareProcessor.ProfileMetricValue lft = fst.myMetrics.get(column.key);
            ProfileCompareProcessor.ProfileMetricValue rgt = snd.myMetrics.get(column.key);

            objects[i][0] = column.name;
            objects[i][1] = column.format(lft.first);
            objects[i][2] = column.format(rgt.first);
            objects[i][3] = diff(lft.first, rgt.first);
        }
        return objects;
    }

    @Override
    public Object[] formatTableColumns(List<ProfileCompareProcessor.ProfileCompareColumn> columns) {
        return TABLE_COLUMNS;
    }
}
