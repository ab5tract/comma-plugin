package org.raku.heapsnapshot.run;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.raku.heapsnapshot.HeapSnapshotCollection;
import org.raku.heapsnapshot.ui.HeapSnapshotView;
import org.raku.heapsnapshot.ui.RakuHeapSnapshotResultsPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.RandomAccessFile;

public class RakuLoadHeapSnapshotFileTask extends Task.Backgroundable {
    public static final Logger LOG = Logger.getInstance(RakuLoadHeapSnapshotFileTask.class);
    private final HeapSnapshotView heapSnapshotView;
    private final File heapSnapshotFile;
    private HeapSnapshotCollection snapshotCollection;

    public RakuLoadHeapSnapshotFileTask(Project project, String data, boolean canBeCancelled,
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
                RakuHeapSnapshotResultsPanel view = new RakuHeapSnapshotResultsPanel(myProject, snapshotCollection);
                heapSnapshotView.setView(view);
            });
        }
        catch (Exception e) {
            Notifications.Bus.notify(new Notification("raku.heap.snapshot.recorder",
                 "Error parsing heap snapshot results",
                 e.getMessage() != null ? e.getMessage() : "Unknown error", NotificationType.ERROR));
            heapSnapshotView.setView(new JLabel("Could not parse heap snapshot results"));
            throw new ProcessCanceledException();
        }
    }
}
