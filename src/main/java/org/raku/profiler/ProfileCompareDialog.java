package org.raku.profiler;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import org.raku.profiler.compare.ProfileCompareProcessor;
import org.raku.profiler.compare.ProfileCompareTab;
import org.raku.profiler.model.RakuProfileData;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileCompareDialog extends DialogWrapper {
    private final static Logger LOG = Logger.getInstance(ProfileCompareDialog.class);
    private final ProfileCompareProcessor.ProfileCompareResults myResults;
    private final String leftName;
    private final String rightName;

    public ProfileCompareDialog(Project project,
                                RakuProfileData[] profiles,
                                ProfileCompareProcessor.ProfileCompareResults results) {
        super(project, true);
        myResults = results;
        leftName = profiles[0].getName();
        rightName = profiles[1].getName();
        setTitle("Comparing " + profiles[0].getName() + " (A) with " + profiles[1].getName() + " (B)");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        final JBTabbedPane tabbedPane = new JBTabbedPane();
        List<ProfileCompareTab> tabs = myResults.getTabs();
        for (int i = 0; i < tabs.size(); i++) {
            ProfileCompareTab tab = tabs.get(i);
            final int _i = i;
            tabbedPane.insertTab(tab.tabName, null, new JBLabel("Loading", AnimatedIcon.Default.INSTANCE, SwingConstants.LEFT), null, i);
            tab.onDataReady(data -> {
                LOG.info("Data loaded! #" + _i + " (" + tab.dataProvider.getClass().getName() + ")");
                try {
                    JBTable table = getTable(tab, data);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        LOG.info("Data loaded later... #" + _i + " (" + tab.dataProvider.getClass().getName() + ")");
                        tabbedPane.setComponentAt(_i, new JBScrollPane(table));
                    }, ModalityState.any());
                } catch (Exception e) {
                    LOG.error(e);
                }
            });
        }

        tabbedPane.setPreferredSize(new Dimension(600, 800));
        JPanel panel = new JPanel(new MigLayout());
        panel.add(new JLabel("A: " + leftName), "wrap");
        panel.add(new JLabel("B: " + rightName), "wrap");
        panel.add(tabbedPane, "grow, push");
        return panel;
    }

    @NotNull
    private static JBTable getTable(ProfileCompareTab tab, Object[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, tab.getTableColumns());
        JBTable table = new JBTable(model);
        table.setDefaultEditor(Object.class, null);

        // Install a StringToIntComparator for every column, since
        // 1) We don't exactly know which column is which
        // 2) The comparator happily falls back to string comparison if it's a non-int column
        TableRowSorter<TableModel> trs = new TableRowSorter<>(model);
        for (int i = 0; i < model.getColumnCount(); i++) {
            trs.setComparator(i, new StringToIntComparator());
        }

        table.setRowSorter(trs);

        return table;
    }

    /* Comparator class that tries to recover the int value from the presented value.
    * Also tries to extract the int value from a comparison column, i.e. "+3 (+0.3%)" */
    private static class StringToIntComparator implements Comparator<String> {
        private static final Pattern INT_PATTERN = Pattern.compile("^[+-]?[\\d,.]+");

        @Override
        public int compare(String left, String right) {
            Matcher leftMatcher = INT_PATTERN.matcher(left);
            Matcher rightMatcher = INT_PATTERN.matcher(right);
            if (leftMatcher.find() && rightMatcher.find()) {
                String leftValue = leftMatcher.group(0).replace(",", "").replace(".", "");
                String rightValue = rightMatcher.group(0).replace(",", "").replace(".", "");
                return Integer.compare(Integer.parseInt(leftValue), Integer.parseInt(rightValue));
            }
            return left.compareTo(right);
        }
    }
}
