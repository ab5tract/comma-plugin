package edument.perl6idea.heapsnapshot.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.runners.ExecutionEnvironment;
import edument.perl6idea.heapsnapshot.run.Perl6HeapSnapshotCommandLineState;
import edument.perl6idea.ui.CustomConsoleRunTab;

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
        if (!(uiUpdater instanceof Perl6HeapSnapshotCommandLineState))
            throw new ExecutionException("Expected Heap Snapshot state, got " + uiUpdater.getClass() + " instead");
        // TODO
    }
}
