package edument.perl6idea.heapsnapshot.run;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import edument.perl6idea.heapsnapshot.HeapSnapshotCollection;
import edument.perl6idea.heapsnapshot.ui.HeapSnapshotView;
import edument.perl6idea.heapsnapshot.ui.Perl6HeapSnapshotResultsPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Perl6LoadHeapSnapshotFileTask extends Task.Backgroundable {
    public static final Logger LOG = Logger.getInstance(Perl6LoadHeapSnapshotFileTask.class);
    private final HeapSnapshotView heapSnapshotView;
    private File heapSnapshotFile;
    private HeapSnapshotCollection snapshotCollection;

    public Perl6LoadHeapSnapshotFileTask(Project project, String data, boolean canBeCancelled,
                            File file, HeapSnapshotView heapSnapshotView) {
        super(project, data, canBeCancelled);
        this.heapSnapshotFile = file;
        this.heapSnapshotView = heapSnapshotView;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        try {
            indicator.setIndeterminate(false);
            indicator.setText("Parsing heap snapshot table of contents");
            indicator.setFraction(0.1);
            indicator.checkCanceled();
            snapshotCollection = new HeapSnapshotCollection(new RandomAccessFile(heapSnapshotFile, "r"));
            indicator.setText("Heap snapshot parsing is finished");
            indicator.setFraction(1);
            ApplicationManager.getApplication().invokeLater(() -> {
                Perl6HeapSnapshotResultsPanel view = new Perl6HeapSnapshotResultsPanel(myProject, snapshotCollection);
                heapSnapshotView.setView(view);
            });
        }
        catch (Exception e) {
            Notifications.Bus.notify(new Notification("Raku Heap Snapshot Recorder",
                 "Error parsing heap snapshot results",
                 e.getMessage() != null ? e.getMessage() : "Unknown error", NotificationType.ERROR));
            heapSnapshotView.setView(new JLabel("Could not parse heap snapshot results"));
            throw new ProcessCanceledException();
        }
    }
}
