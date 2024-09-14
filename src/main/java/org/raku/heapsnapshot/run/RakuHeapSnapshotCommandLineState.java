package org.raku.heapsnapshot.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import org.raku.run.RakuRunCommandLineState;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class RakuHeapSnapshotCommandLineState extends RakuRunCommandLineState {
    static Logger LOG = Logger.getInstance(RakuHeapSnapshotCommandLineState.class);
    private File tempFile = null;

    public RakuHeapSnapshotCommandLineState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    protected void populateRunCommand() throws ExecutionException {
        String canonicalPath;
        try {
            tempFile = FileUtil.createTempFile("comma-heap-snapshots", ".mvmheap");
            canonicalPath = tempFile.getCanonicalPath();
        }
        catch (IOException e) {
            LOG.warn(e);
            throw new ExecutionException(e.getMessage());
        }
        command.add(String.format("--profile=%s", canonicalPath));
        setInterpreterParameters();
    }

    @Nullable
    public File getSnapshotsFile() {
        return tempFile;
    }
}
