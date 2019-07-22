package edument.perl6idea.profiler.model;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;

public class Perl6ProfileOutputTableRowSorter extends TableRowSorter<Perl6ProfileNavigationModel> {
    public Perl6ProfileOutputTableRowSorter(Perl6ProfileNavigationModel model) {
        super(model);
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
