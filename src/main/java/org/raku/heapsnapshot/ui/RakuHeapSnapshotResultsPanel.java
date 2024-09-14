package org.raku.heapsnapshot.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import org.raku.heapsnapshot.HeapSnapshotCollection;

import javax.swing.*;
import java.awt.*;

public class RakuHeapSnapshotResultsPanel extends JPanel {
    private final Project project;

    public RakuHeapSnapshotResultsPanel(Project project, HeapSnapshotCollection snapshotCollection) {
        super(new BorderLayout());
        this.project = project;

        JBTabbedPane tabs = new JBTabbedPane();
        tabs.addTab("Overview", new JBScrollPane(new HeapSnapshotSummaryTab(snapshotCollection)));
        tabs.addTab("Browser", new HeapSnapshotBrowserTab(snapshotCollection));
        add(tabs);
    }
}
