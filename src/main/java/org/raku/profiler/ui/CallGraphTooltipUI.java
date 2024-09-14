package org.raku.profiler.ui;

import com.intellij.ui.JBColor;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import org.raku.profiler.model.RakuProfileCall;
import org.raku.utils.RakuUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class CallGraphTooltipUI extends JPanel {
    // Fonts
    private final Font labelFont = JBUI.Fonts.label();
    private final Font callNameFont = labelFont.deriveFont((float)labelFont.getSize() + 4).deriveFont(Font.BOLD);
    private final Font callTableLabelFont = labelFont.deriveFont((float)labelFont.getSize() + 2).deriveFont(Font.BOLD);

    private final RakuProfileCall myCall;
    private JPanel myMainPanel;

    public CallGraphTooltipUI(RakuProfileCallGraph.CallItem call) {
        myCall = call.myCall;
        setupUI();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    private void setupUI() {
        myMainPanel = new JPanel(new GridBagLayout());
        myMainPanel.setBorder(new EmptyBorder(JBUI.insets(5)));

        // Set names, file paths
        JLabel callNameLabel = new JLabel(myCall.getName());
        callNameLabel.setFont(callNameFont);
        addToPanel(callNameLabel, 0, 0, 1, JBUI.insets(10));

        String moduleName = myCall.getModuleName();
        if (moduleName != null) {
            JLabel moduleNameLabel = new JLabel(moduleName);
            moduleNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            moduleNameLabel.setForeground(JBColor.GRAY);
            addToPanel(moduleNameLabel, 1, 0, 1, JBUI.insets(10));
        }

        JLabel pathLabel = new JLabel(myCall.getOriginalFile());
        addToPanel(pathLabel, 0, 1, 2, JBUI.insets(10, 5));

        // Set number data
        int time = myCall.getInclusiveTime();
        JLabel totalLabel = new JLabel("Total");
        addToPanel(totalLabel, 0, 2);
        JLabel totalValueLabel = new JLabel(RakuUtils.formatDelimiters(time, ",", 3) + "μ");
        totalValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        addToPanel(totalValueLabel, 1, 2);

        JLabel entriesLabel = new JLabel("Entries");
        addToPanel(entriesLabel, 0, 3);
        JLabel entriesValueLabel = new JLabel(RakuUtils.formatDelimiters(myCall.getEntriesCount(), ",", 3));
        entriesValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        addToPanel(entriesValueLabel, 1, 3);

        JLabel perEntryLabel = new JLabel("Per entry");
        addToPanel(perEntryLabel, 0, 4);
        JLabel perEntryValueLabel = new JLabel(RakuUtils.formatDelimiters((int)myCall.averageCallTime(), ",", 3) + "μ");
        perEntryValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        addToPanel(perEntryValueLabel, 1, 4);

        JLabel inlinedLabel = new JLabel("Inlined");
        addToPanel(inlinedLabel, 0, 5);
        JLabel inlinedValueLabel = new JLabel(myCall.inlined() + "%");
        inlinedValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        addToPanel(inlinedValueLabel, 1, 5);

        JLabel speshLabel = new JLabel("Spesh");
        addToPanel(speshLabel, 0, 6);
        JLabel speshValueLabel = new JLabel((myCall.spesh() + "%"));
        speshValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        addToPanel(speshValueLabel, 1, 6);

        // Set children table

        JLabel calleeTableLabel = new JLabel("Callees");
        calleeTableLabel.setFont(callTableLabelFont);
        calleeTableLabel.setHorizontalAlignment(SwingConstants.CENTER);
        addToPanel(calleeTableLabel, 0, 8, 2, JBUI.insets(0,0,10,0));

        List<RakuProfileCall> callees = myCall.getCallees();
        callees.sort((c1, c2) -> Integer.compare(c2.getInclusiveTime(), c1.getInclusiveTime()));
        CalleeTableModel model = new CalleeTableModel(callees.subList(0, Math.min(callees.size(), 15)));
        JBTable calleeTable = new JBTable(model);
        addToPanel(calleeTable.getTableHeader(), 0, 9, 2, JBUI.insets(0));
        addToPanel(calleeTable, 0, 10, 2, JBUI.insets(0));
    }

    private void addToPanel(JComponent comp, int x, int y) {
        addToPanel(comp, x, y, 1, JBUI.insets(2));
    }

    private void addToPanel(JComponent comp, int x, int y, int width, Insets insets) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridwidth = width;
        c.insets = insets;
        c.gridx = x;
        c.gridy = y;
        myMainPanel.add(comp, c);
    }

    private static class CalleeTableModel extends AbstractTableModel {
        public static final String[] CALLEE_TABLE_COLUMNS = new String[]{"Callee", "Entries", "Inlined %", "Time"};
        private final List<RakuProfileCall> myCallees;

        CalleeTableModel(List<RakuProfileCall> list) {
            myCallees = list;
        }

        @Override
        public int getRowCount() {
            return myCallees.size();
        }

        @Override
        public int getColumnCount() {
            return CALLEE_TABLE_COLUMNS.length;
        }

        @Override
        public String getColumnName(int column) {
            return CALLEE_TABLE_COLUMNS[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            RakuProfileCall call = myCallees.get(rowIndex);
            switch (columnIndex) {
                case 0: return call.getName();
                case 1: return call.getEntriesCount();
                case 2: return call.inlined();
                default: {
                    RakuProfileCall parent = call.getParent();
                    float parentInclusiveTime = parent != null ? parent.getInclusiveTime() : call.getInclusiveTime();
                    return
                        String.format(
                            "%.3f%%", 100 * (float)call.getInclusiveTime() / parentInclusiveTime);
                }
            }
        }
    }
}
