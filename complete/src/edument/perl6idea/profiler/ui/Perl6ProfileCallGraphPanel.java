package edument.perl6idea.profiler.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.components.JBScrollPane;
import edument.perl6idea.profiler.model.Perl6ProfileData;
import edument.perl6idea.profiler.model.Perl6ProfileThread;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Perl6ProfileCallGraphPanel extends JPanel {
    private final Perl6ProfileData myProfileData;
    private final JBScrollPane myScrollPane;
    private final Perl6ProfileCallGraph myProfileCallGraph;
    private List<Perl6ProfileThread> myThreads;
    private JLabel myTimeLabel;

    public Perl6ProfileCallGraphPanel(Project project, Perl6ProfileData data) {
        super(new BorderLayout());
        myProfileData = data;

        // Create chart configuration means
        JPanel configPanel = createConfigPanel();
        add(configPanel, BorderLayout.NORTH);

        // Prepare chart area
        myScrollPane = new JBScrollPane();
        myProfileCallGraph = new Perl6ProfileCallGraph(project, data, this);
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
