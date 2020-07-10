package edument.perl6idea.heapsnapshot.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import edument.perl6idea.heapsnapshot.ui.HeapSnapshotContentBuilder;
import edument.perl6idea.profiler.ui.ProfileContentBuilder;
import edument.perl6idea.run.Perl6HeapSnapshotExecutor;
import edument.perl6idea.run.Perl6ProfileExecutor;
import edument.perl6idea.run.Perl6RunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Perl6HeapSnapshotRunner extends DefaultProgramRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "Raku Heap Snapshot";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return Objects.equals(Perl6HeapSnapshotExecutor.EXECUTOR_ID, executorId) &&
               profile instanceof Perl6RunConfiguration;
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env)
            throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();
        ExecutionResult executionResult = state.execute(env.getExecutor(), this);
        return showRunContent(executionResult, env, ((Perl6HeapSnapshotCommandLineState)state));
    }

    private static RunContentDescriptor showRunContent(ExecutionResult result, ExecutionEnvironment env, Perl6HeapSnapshotCommandLineState state)
            throws ExecutionException {
        return result != null
               ? new HeapSnapshotContentBuilder(result, env).showRunContent(env.getContentToReuse(), state)
               : null;
    }
}
