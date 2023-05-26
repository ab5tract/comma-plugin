package edument.perl6idea.heapsnapshot.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import edument.perl6idea.heapsnapshot.HeapSnapshotCollection;

import javax.swing.*;
import java.awt.*;

public class Perl6HeapSnapshotResultsPanel extends JPanel {
    private final Project project;

    public Perl6HeapSnapshotResultsPanel(Project project, HeapSnapshotCollection snapshotCollection) {
        super(new BorderLayout());
        this.project = project;

        JBTabbedPane tabs = new JBTabbedPane();
        tabs.addTab("Overview", new JBScrollPane(new HeapSnapshotSummaryTab(snapshotCollection)));
        tabs.addTab("Browser", new HeapSnapshotBrowserTab(snapshotCollection));
        add(tabs);
    }
}
