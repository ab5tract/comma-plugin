package edument.perl6idea.profiler.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import edument.perl6idea.profiler.model.Perl6ProfileCall;
import edument.perl6idea.profiler.model.Perl6ProfileData;

import javax.swing.*;
import java.awt.*;

public class Perl6ProfileResultsPanel extends JPanel {
    private Project myProject;
    private Perl6ProfileData myProfileData;
    private final JBTabbedPane myTabbedPaneWrapper;
    private JPanel myRoutinesView;
    private JScrollPane myCallGraphView;

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
        JScrollPane pane = new JBScrollPane();
        Perl6ProfileCallGraph view = new Perl6ProfileCallGraph(myProject, myProfileData, pane);
        pane.setViewportView(view);
        myCallGraphView = pane;
        return myCallGraphView;
    }

    private Component getRoutinesTab() {
        myRoutinesView = new Perl6ProfileRoutinesPanel(myProject, myProfileData);
        return ((Perl6ProfileRoutinesPanel)myRoutinesView).getPanel();
    }
}
