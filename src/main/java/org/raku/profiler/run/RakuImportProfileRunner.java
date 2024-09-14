package org.raku.profiler.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.search.GlobalSearchScope;
import org.raku.debugger.RakuDefaultRunner;
import org.raku.profiler.ui.ProfileContentBuilder;
import org.raku.profiler.ui.ProfilerView;
import org.raku.run.RakuProfileExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RakuImportProfileRunner extends RakuDefaultRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "Raku Profiler By Import";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return Objects.equals(RakuProfileExecutor.EXECUTOR_ID, executorId) &&
               profile instanceof RakuImportRunner;
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        ExecutionResult executionResult = new ExecutionResult() {
            @Override
            public ExecutionConsole getExecutionConsole() {
                return TextConsoleBuilderFactory.getInstance().createBuilder(env.getProject(), GlobalSearchScope.allScope(env.getProject()))
                    .getConsole();
            }

            @Override
            public AnAction @NotNull [] getActions() {
                return AnAction.EMPTY_ARRAY;
            }

            @Override
            public ProcessHandler getProcessHandler() {
                return null;
            }
        };
        return showRunContent(executionResult, env, ((RakuProfileCommandLineState)state));
    }

    private static RunContentDescriptor showRunContent(ExecutionResult result, ExecutionEnvironment env, RakuProfileCommandLineState state)
        throws ExecutionException {
        return result != null
               ? new ProfileContentBuilder(result, env) {
            @Override
            protected void loadProfileResults(RakuProfileCommandLineState uiUpdater, ProfilerView profilerView) {
                if (uiUpdater.hasFile())
                    profilerView.updateResultsFromFile(state.getProfileResultsFile(), false);
                else if (uiUpdater.hasData())
                    profilerView.updateResultsFromData(state.getProfilerResultData());
            }
        }.showRunContent(env.getContentToReuse(), state)
               : null;
    }
}
