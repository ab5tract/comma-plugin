package edument.perl6idea.profiler.compare;

import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;

import java.util.Arrays;
import java.util.List;

public class ByNameCompareFormatter extends CompareFormatter {
    public static final ByNameCompareFormatter SINGLE_NAME = new ByNameCompareFormatter(false);
    public static final ByNameCompareFormatter BOTH_NAMES = new ByNameCompareFormatter(true);
    private final boolean bothNames;

    private ByNameCompareFormatter(boolean bothNames) {
        this.bothNames = bothNames;
    }

    @Override
    public Object[][] format(List<ProfileCompareProcessor.ProfileCompareRow> rows, List<Pair<String, String>> columns) {
          int skippedRows = 0;
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

              if (isRowEmpty(result, columns)) {
                  skippedRows++;
                  continue;
              }

              for (int j = 0; j < columns.size(); j++) {
                  String metricKey = columns.get(j).second;
                  ProfileCompareProcessor.ProfileMetricValue metricData = result.myMetrics.get(metricKey);
                  allData[i][offset + j * 3] = metricData.first();
                  allData[i][offset + 1 + j * 3] = metricData.second();
                  allData[i][offset + 2 + j * 3] = diff(metricData);
              }
          }

          // Remove the skipped rows (empty in `allData`)
          int rowsLeft = rows.size() - skippedRows;
          Object[][] data = new Object[rowsLeft][offset + 3 * columns.size()];
          System.arraycopy(allData, 0, data, 0, rowsLeft);

          return data;
    }

    // Skip rows for which all the (used) values are 0 on both sides
    private static boolean isRowEmpty(ProfileCompareProcessor.ProfileCompareRow result, List<Pair<String, String>> columns) {
        for (Pair<String, String> column : columns) {
            ProfileCompareProcessor.ProfileMetricValue metricData = result.myMetrics.get(column.second);
            if (metricData.first != 0 || metricData.second != 0)
                return false;
        }
        return true;
    }

    private int getOffset() {
        return bothNames ? 2 : 1;
    }

    @Override
    public Object[] formatTableColumns(List<Pair<String, String>> columns) {
        int offset = getOffset();
        Object[] columnNames = getColumnNames(columns);
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
