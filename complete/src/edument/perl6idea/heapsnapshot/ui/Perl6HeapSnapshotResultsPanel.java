package edument.perl6idea.heapsnapshot.ui;

import com.intellij.openapi.project.Project;
import edument.perl6idea.heapsnapshot.HeapSnapshotCollection;

import javax.swing.*;

public class Perl6HeapSnapshotResultsPanel extends JPanel {
    private Project project;
    private HeapSnapshotCollection snapshotCollection;

    public Perl6HeapSnapshotResultsPanel(Project project, HeapSnapshotCollection snapshotCollection) {
        this.project = project;
        this.snapshotCollection = snapshotCollection;
    }
}
