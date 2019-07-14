package edument.perl6idea.profiler.ui;

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
    private List<Perl6ProfileThread> myThreads;

    public Perl6ProfileCallGraphPanel(Perl6ProfileData data) {
        super(new BorderLayout());
        myProfileData = data;
        add(new JLabel("Axis Value"), BorderLayout.NORTH);

        // Prepare chart area
        JScrollPane pane = new JBScrollPane();
        Perl6ProfileCallGraph view = new Perl6ProfileCallGraph(data, pane);
        pane.setViewportView(view);
        add(pane, BorderLayout.CENTER);
        // Create chart configuration means
        JPanel configPanel = createConfigPanel(view);
        add(configPanel, BorderLayout.NORTH);
    }

    private JPanel createConfigPanel(Perl6ProfileCallGraph view) {
        JPanel configPanel = new JPanel(new BorderLayout());
        LabeledComponent<JComboBox> threadBox = prepareThreadBox(view);
        configPanel.add(threadBox, BorderLayout.WEST);
        configPanel.add(new JLabel("AXIS LABEL TIME"), BorderLayout.CENTER);
        configPanel.add(new JComboBox<>(), BorderLayout.EAST);
        return configPanel;
    }

    @NotNull
    private LabeledComponent<JComboBox> prepareThreadBox(Perl6ProfileCallGraph view) {
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
                view.switchToThread(myThreads.get(threadIndex).rootNodeID);
            }
        });
        threadBox.setComponent(comboBox);
        return threadBox;
    }
}
