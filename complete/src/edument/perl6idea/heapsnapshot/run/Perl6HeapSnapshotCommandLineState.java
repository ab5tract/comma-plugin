package edument.perl6idea.heapsnapshot.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import edument.perl6idea.run.Perl6RunCommandLineState;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class Perl6HeapSnapshotCommandLineState extends Perl6RunCommandLineState {
    static Logger LOG = Logger.getInstance(Perl6HeapSnapshotCommandLineState.class);
    private File tempFile = null;

    public Perl6HeapSnapshotCommandLineState(ExecutionEnvironment environment) {
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
