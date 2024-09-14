package org.raku.comma.profiler.compare;

import java.util.List;
import java.util.function.Consumer;

public class ProfileCompareTab {
    public final String tabName;
    public final List<ProfileCompareProcessor.ProfileCompareColumn> columns;
    public final ProfileCompareDataProvider dataProvider;
    private final CompareFormatter formatter;

    public ProfileCompareTab(String tabName, List<ProfileCompareProcessor.ProfileCompareColumn> columns, ProfileCompareDataProvider dataProvider, CompareFormatter formatter) {
        this.tabName = tabName;
        this.columns = columns;
        this.dataProvider = dataProvider;
        this.formatter = formatter;
    }

    public void onDataReady(Consumer<Object[][]> callback) {
        dataProvider.onDataReady(rows -> callback.accept(formatter.format(rows, columns)));
    }

    public Object[] getTableColumns() {
        return formatter.formatTableColumns(columns);
    }
}
