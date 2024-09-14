package org.raku.comma.profiler.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import org.raku.comma.debugger.RakuDefaultRunner;
import org.raku.comma.profiler.ui.ProfileContentBuilder;
import org.raku.comma.run.RakuProfileExecutor;
import org.raku.comma.run.RakuRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RakuProfileRunner extends RakuDefaultRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "Raku Profiler";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return Objects.equals(RakuProfileExecutor.EXECUTOR_ID, executorId) &&
               profile instanceof RakuRunConfiguration;
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env)
        throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();
        ExecutionResult executionResult = state.execute(env.getExecutor(), this);
        return showRunContent(executionResult, env, ((RakuProfileCommandLineState)state));
    }

    private static RunContentDescriptor showRunContent(ExecutionResult result, ExecutionEnvironment env, RakuProfileCommandLineState state)
        throws ExecutionException {
        return result != null
               ? new ProfileContentBuilder(result, env).showRunContent(env.getContentToReuse(), state)
               : null;
    }
}
