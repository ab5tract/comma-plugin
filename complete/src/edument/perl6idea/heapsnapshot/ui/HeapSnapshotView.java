package edument.perl6idea.heapsnapshot.ui;

import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import edument.perl6idea.heapsnapshot.run.Perl6LoadHeapSnapshotFileTask;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class HeapSnapshotView extends JPanel {
    private final Project myProject;
    private JComponent myView;

    public HeapSnapshotView(Project project) {
        super();
        myProject = project;
        setLayout(new BorderLayout());
        myView = new JLabel("Waiting for program to terminate to analyze snapshot...");
        add(myView, BorderLayout.CENTER);
    }

    public void updateResultsFromFile(File file) {
        if (file == null) {
            setView(new JLabel("Error during results connecting: heap snapshot file was absent"));
            return;
        }
        Task.Backgroundable task = new Perl6LoadHeapSnapshotFileTask(myProject, "Processing Heap Snapshots", true, file, this);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }

    public void setView(JComponent view) {
        remove(myView);
        myView = view;
        add(myView, BorderLayout.CENTER);
    }
}
