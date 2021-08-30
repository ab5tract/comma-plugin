package edument.perl6idea.profiler.run;

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
import edument.perl6idea.debugger.Perl6DefaultRunner;
import edument.perl6idea.profiler.ui.ProfileContentBuilder;
import edument.perl6idea.profiler.ui.ProfilerView;
import edument.perl6idea.run.Perl6ProfileExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Perl6ImportProfileRunner extends Perl6DefaultRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "Raku Profiler By Import";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return Objects.equals(Perl6ProfileExecutor.EXECUTOR_ID, executorId) &&
               profile instanceof Perl6ImportRunner;
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
        return showRunContent(executionResult, env, ((Perl6ProfileCommandLineState)state));
    }

    private static RunContentDescriptor showRunContent(ExecutionResult result, ExecutionEnvironment env, Perl6ProfileCommandLineState state)
        throws ExecutionException {
        return result != null
               ? new ProfileContentBuilder(result, env) {
            @Override
            protected void loadProfileResults(Perl6ProfileCommandLineState uiUpdater, ProfilerView profilerView) {
                if (uiUpdater.hasFile())
                    profilerView.updateResultsFromFile(state.getProfileResultsFile(), false);
                else if (uiUpdater.hasData())
                    profilerView.updateResultsFromData(state.getProfilerResultData());
            }
        }.showRunContent(env.getContentToReuse(), state)
               : null;
    }
}
