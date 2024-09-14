package org.raku.comma.profiler.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.components.JBScrollPane;
import org.raku.comma.profiler.model.RakuProfileData;
import org.raku.comma.profiler.model.Perl6ProfileThread;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RakuProfileCallGraphPanel extends JPanel {
    private final RakuProfileData myProfileData;
    private final JBScrollPane myScrollPane;
    private final RakuProfileCallGraph myProfileCallGraph;
    private List<Perl6ProfileThread> myThreads;
    private JLabel myTimeLabel;

    public RakuProfileCallGraphPanel(Project project, RakuProfileData data) {
        super(new BorderLayout());
        myProfileData = data;

        // Create chart configuration means
        JPanel configPanel = createConfigPanel();
        add(configPanel, BorderLayout.NORTH);

        // Prepare chart area
        myScrollPane = new JBScrollPane();
        myProfileCallGraph = new RakuProfileCallGraph(project, data, this);
        myScrollPane.setViewportView(myProfileCallGraph);
        add(myScrollPane, BorderLayout.CENTER);
    }

    private JPanel createConfigPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());

        LabeledComponent<JComboBox> threadBox = prepareThreadBox();
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        configPanel.add(threadBox, c);

        myTimeLabel = new JLabel();
        myTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        configPanel.add(myTimeLabel, c);

        return configPanel;
    }

    @NotNull
    private LabeledComponent<JComboBox> prepareThreadBox() {
        LabeledComponent<JComboBox> threadBox = new LabeledComponent<>();
        threadBox.setLabelLocation(BorderLayout.WEST);
        threadBox.setText("Thread");

        JComboBox<String> comboBox = new ComboBox<>();
        myThreads = myProfileData.getThreads();
        for (Perl6ProfileThread thread : myThreads) {
            comboBox.addItem(String.format("Thread # %s", thread.threadID));
        }
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int threadIndex = comboBox.getSelectedIndex();
                myProfileCallGraph.switchToThread(myThreads.get(threadIndex).rootNodeID);
            }
        });
        threadBox.setComponent(comboBox);
        return threadBox;
    }

    public JBScrollPane getScrollPane() {
        return myScrollPane;
    }

    public JLabel getTimeLabel() {
        return myTimeLabel;
    }
}
