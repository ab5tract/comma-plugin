package edument.perl6idea.profiler.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import edument.perl6idea.profiler.model.Perl6ProfileData;
import edument.perl6idea.profiler.model.Perl6ProfileThread;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Perl6ProfileResultsPanel extends JPanel {
    private Project myProject;
    private Perl6ProfileData myProfileData;
    private final JBTabbedPane myTabbedPaneWrapper;
    private JPanel myRoutinesView;
    private JPanel myCallGraphView;
    private List<Perl6ProfileThread> myThreads;

    public Perl6ProfileResultsPanel(Project project,
                                    Perl6ProfileData profileData) {
        super(new BorderLayout());
        myProject = project;
        myProfileData = profileData;
        myTabbedPaneWrapper = new JBTabbedPane();
        myTabbedPaneWrapper.addTab("Routines", getRoutinesTab());
        myTabbedPaneWrapper.addTab("Call Graph", getCallGraphTab());
        add(new JScrollPane(myTabbedPaneWrapper), BorderLayout.CENTER);
    }

    private Component getCallGraphTab() {
        // Prepare chart area
        JScrollPane pane = new JBScrollPane();
        Perl6ProfileCallGraph view = new Perl6ProfileCallGraph(myProject, myProfileData, pane);
        pane.setViewportView(view);
        // Create thread chooser combo-box
        LabeledComponent<JComboBox> threadBox = prepareThreadBox(view);

        // Create a main panel and add parts there
        JPanel callGraphPanel = new JPanel(new BorderLayout());
        callGraphPanel.add(threadBox, BorderLayout.NORTH);
        callGraphPanel.add(pane, BorderLayout.CENTER);
        myCallGraphView = callGraphPanel;
        return myCallGraphView;
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

    private Component getRoutinesTab() {
        myRoutinesView = new Perl6ProfileRoutinesPanel(myProject, myProfileData);
        return ((Perl6ProfileRoutinesPanel)myRoutinesView).getPanel();
    }
}
