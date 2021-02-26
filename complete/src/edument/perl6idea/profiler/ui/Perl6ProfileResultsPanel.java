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
        tabbedPaneWrapper.addTab("Modules", getModulesTab());
        tabbedPaneWrapper.addTab("GC", getGCTab());
        tabbedPaneWrapper.addTab("Allocations", getAllocationsTab());
        add(tabbedPaneWrapper, BorderLayout.CENTER);
    }

    private Component getRoutinesTab() {
        return new Perl6ProfileRoutinesPanel(myProject, myProfileData).getPanel();
    }

    private Component getCallGraphTab() {
        return new Perl6ProfileCallGraphPanel(myProject, myProfileData);
    }

    private Component getModulesTab() {
        return new Perl6ProfileModulesPanel(myProfileData);
    }

    private Component getGCTab() {
        return new Perl6ProfileGCPanel(myProfileData);
    }

    private Component getAllocationsTab() {
        return new Perl6ProfileAllocationsPanel(myProfileData).getPanel();
    }
}
