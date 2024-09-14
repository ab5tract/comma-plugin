package org.raku.profiler.model;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.Collections;

public class RakuProfileOutputTableRowSorter extends TableRowSorter<RakuProfileNavigationModel> {
    public RakuProfileOutputTableRowSorter(RakuProfileNavigationModel model) {
        super(model);
        setSortKeys(Collections.singletonList(new SortKey(2, SortOrder.DESCENDING)));
    }

    @Override
    public void toggleSortOrder(int column) {
        if (column <= 1) {
            super.toggleSortOrder(column);
            return;
        }
        ArrayList<SortKey> sortKeys = new ArrayList<>(getSortKeys());
        if (sortKeys.isEmpty() || sortKeys.get(0).getColumn() != column) {
            sortKeys.add(0, new RowSorter.SortKey(column, SortOrder.DESCENDING));
        }
        else if (sortKeys.get(0).getSortOrder() == SortOrder.ASCENDING) {
            sortKeys.removeIf(key -> key.getColumn() == column);
            sortKeys.add(0, new RowSorter.SortKey(column, SortOrder.DESCENDING));
        }
        else {
            sortKeys.removeIf(key -> key.getColumn() == column);
            sortKeys.add(0, new RowSorter.SortKey(column, SortOrder.ASCENDING));
        }
        setSortKeys(sortKeys);
    }
}
