package org.raku.comma.timeline;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import org.raku.comma.debugger.RakuDefaultRunner;
import org.raku.comma.run.RakuRunConfiguration;
import org.raku.comma.timeline.client.TimelineClient;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class RakuTimelineRunner extends RakuDefaultRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "Raku Timeline";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return RakuTimelineExecutor.EXECUTOR_ID.equals(executorId) &&
               (profile instanceof RakuRunConfiguration);
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();
        ExecutionResult executionResult = state.execute(env.getExecutor(), this);
        return showRunContent(executionResult, env, ((RakuTimelineCommandLineState)state).getTimelineClient());
    }

    private static RunContentDescriptor showRunContent(ExecutionResult execute,
                                                       ExecutionEnvironment env,
                                                       TimelineClient client) throws ExecutionException {
        return execute != null
                ? new TimelineContentBuilder(execute, env).showRunContent(env.getContentToReuse(), client)
                : null;
    }
}
