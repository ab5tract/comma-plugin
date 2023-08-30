package edument.perl6idea.profiler.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTabbedPane;
import edument.perl6idea.profiler.model.Perl6ProfileData;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Perl6ProfileResultsPanel extends JPanel {
    private final Project myProject;
    private final Perl6ProfileData[] myProfileSnapshots;

    public Perl6ProfileResultsPanel(Project project,
                                    Perl6ProfileData[] profileSnapshots) {
        super(new BorderLayout());
        myProject = project;
        myProfileSnapshots = profileSnapshots;

        init();
    }

    private void init() {
        JBTabbedPane profileDataTabs = new JBTabbedPane();
        List<JBTabbedPane> profileViewsTabs = new ArrayList<>();

        for (Perl6ProfileData profileData : myProfileSnapshots) {
            JBTabbedPane tabs = new JBTabbedPane();
            tabs.addTab("Overview", new Perl6ProfileOverviewPanel(profileData).getPanel());
            tabs.addTab("Routines", new Perl6ProfileRoutinesPanel(myProject, profileData).getPanel());
            tabs.addTab("Call Graph", new Perl6ProfileCallGraphPanel(myProject, profileData));
            tabs.addTab("Modules", new Perl6ProfileModulesPanel(profileData));
            tabs.addTab("GC", new Perl6ProfileGCPanel(profileData));
            tabs.addTab("Allocations", new Perl6ProfileAllocationsPanel(profileData).getPanel());
            profileDataTabs.addTab(profileData.getName(), tabs);
            profileViewsTabs.add(tabs);
        }

        if (myProfileSnapshots.length == 1)
            add(profileViewsTabs.get(0), BorderLayout.CENTER);
        else {
            add(profileDataTabs, BorderLayout.CENTER);
        }

    }
}
