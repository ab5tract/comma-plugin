package edument.perl6idea.profiler.compare;

import com.intellij.openapi.util.Pair;

import java.util.List;

public class SingleRowCompareFormatter extends CompareFormatter {
    public static final SingleRowCompareFormatter INSTANCE = new SingleRowCompareFormatter();
    private static final Object[] TABLE_COLUMNS = new Object[] {
        "Metric", "A", "B", "%"
    };

  @Override
    public Object[][] format(List<ProfileCompareProcessor.ProfileCompareRow> rows, List<Pair<String, String>> columns) {
        Object[][] objects = new Object[columns.size()][4];
        ProfileCompareProcessor.ProfileCompareRow fst = rows.get(0);
        ProfileCompareProcessor.ProfileCompareRow snd = rows.get(1);
        for (int i = 0; i < columns.size(); i++) {
            Pair<String, String> column = columns.get(i);
            ProfileCompareProcessor.ProfileMetricValue lft = fst.myMetrics.get(column.second);
            ProfileCompareProcessor.ProfileMetricValue rgt = snd.myMetrics.get(column.second);

            objects[i][0] = column.first;
            objects[i][1] = lft.first();
            objects[i][2] = rgt.first();
            objects[i][3] = diff(lft.first, rgt.first);
        }
        return objects;
    }

    @Override
    public Object[] formatTableColumns(List<Pair<String, String>> columns) {
        return TABLE_COLUMNS;
    }
}
