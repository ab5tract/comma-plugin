package org.raku.comma.profiler.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTabbedPane;
import org.raku.comma.profiler.model.RakuProfileData;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RakuProfileResultsPanel extends JPanel {
    private final Project myProject;
    private final RakuProfileData[] myProfileSnapshots;

    public RakuProfileResultsPanel(Project project,
                                   RakuProfileData[] profileSnapshots) {
        super(new BorderLayout());
        myProject = project;
        myProfileSnapshots = profileSnapshots;

        init();
    }

    private void init() {
        JBTabbedPane profileDataTabs = new JBTabbedPane();
        List<JBTabbedPane> profileViewsTabs = new ArrayList<>();

        for (RakuProfileData profileData : myProfileSnapshots) {
            JBTabbedPane tabs = new JBTabbedPane();
            tabs.addTab("Overview", new RakuProfileOverviewPanel(profileData).getPanel());
            tabs.addTab("Routines", new RakuProfileRoutinesPanel(myProject, profileData).getPanel());
            tabs.addTab("Call Graph", new RakuProfileCallGraphPanel(myProject, profileData));
            tabs.addTab("Modules", new RakuProfileModulesPanel(profileData));
            tabs.addTab("GC", new RakuProfileGCPanel(profileData));
            tabs.addTab("Allocations", new RakuProfileAllocationsPanel(profileData).getPanel());
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
