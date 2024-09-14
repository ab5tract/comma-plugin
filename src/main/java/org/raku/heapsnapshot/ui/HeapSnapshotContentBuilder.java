package org.raku.heapsnapshot.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.ui.content.Content;
import org.raku.heapsnapshot.run.RakuHeapSnapshotCommandLineState;
import org.raku.ui.CustomConsoleRunTab;
import org.jetbrains.annotations.NotNull;

public class HeapSnapshotContentBuilder extends CustomConsoleRunTab {
    public static final String HEAP_SNAPSHOT_CONTENT_ID = "HeapSnapshotContent";

    public HeapSnapshotContentBuilder(ExecutionResult result, ExecutionEnvironment env) {
        super(env, result, "Heap Snapshots");
    }

    @Override
    protected String getCustomTabText() {
        return "Heap Snapshots";
    }

    @Override
    protected void addCustomTab(Object uiUpdater) throws ExecutionException {
        if (!(uiUpdater instanceof RakuHeapSnapshotCommandLineState))
            throw new ExecutionException("Expected Heap Snapshot state, got " + uiUpdater.getClass() + " instead");
        HeapSnapshotView heapSnapshotView = new HeapSnapshotView(myProject);
        Content content = myUi.createContent(HEAP_SNAPSHOT_CONTENT_ID, heapSnapshotView, getCustomTabText(),
                                             null, null);
        content.setPreferredFocusableComponent(heapSnapshotView);
        content.setCloseable(false);
        myUi.addContent(content, 0, PlaceInGrid.center, false);
        myExecutionResult.getProcessHandler().addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                heapSnapshotView.updateResultsFromFile(((RakuHeapSnapshotCommandLineState)uiUpdater).getSnapshotsFile());
            }
        });
    }
}
