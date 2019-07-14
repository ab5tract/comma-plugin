package edument.perl6idea.profiler.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTabbedPane;
import edument.perl6idea.profiler.model.Perl6ProfileData;

import javax.swing.*;
import java.awt.*;

public class Perl6ProfileResultsPanel extends JPanel {
    private Project myProject;
    private Perl6ProfileData myProfileData;

    public Perl6ProfileResultsPanel(Project project,
                                    Perl6ProfileData profileData) {
        super(new BorderLayout());
        myProject = project;
        myProfileData = profileData;
        JBTabbedPane tabbedPaneWrapper = new JBTabbedPane();
        tabbedPaneWrapper.addTab("Routines", getRoutinesTab());
        tabbedPaneWrapper.addTab("Call Graph", getCallGraphTab());
        add(tabbedPaneWrapper, BorderLayout.CENTER);
    }

    private Component getCallGraphTab() {
        return new Perl6ProfileCallGraphPanel(myProfileData);
    }

    private Component getRoutinesTab() {
        return new Perl6ProfileRoutinesPanel(myProject, myProfileData).getPanel();
    }
}
