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
        myProject = project;
        myProfileData = profileData;
        myTabbedPaneWrapper = new JBTabbedPane();
        myTabbedPaneWrapper.addTab("Routines", getRoutinesTab());
        myTabbedPaneWrapper.addTab("Call Graph", getCallGraphTab());
        setLayout(new BorderLayout());
        add(new JScrollPane(myTabbedPaneWrapper), BorderLayout.CENTER);
    }

    private Component getCallGraphTab() {
        Perl6ProfileCall rootCall = myProfileData.getProfileCallById(1, 15, null);
        myCallGraphView = new JBScrollPane(new Perl6ProfileCallGraph(myProject, rootCall));
        return myCallGraphView;
    }

    private Component getRoutinesTab() {
        myRoutinesView = new Perl6ProfileRoutinesPanel(myProject, myProfileData);
        return ((Perl6ProfileRoutinesPanel)myRoutinesView).getPanel();
    }
}
