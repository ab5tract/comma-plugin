package edument.perl6idea.profiler.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import edument.perl6idea.debugger.Perl6DefaultRunner;
import edument.perl6idea.profiler.ui.ProfileContentBuilder;
import edument.perl6idea.run.Perl6ProfileExecutor;
import edument.perl6idea.run.Perl6RunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Perl6ProfileRunner extends Perl6DefaultRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "Raku Profiler";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return Objects.equals(Perl6ProfileExecutor.EXECUTOR_ID, executorId) &&
               profile instanceof Perl6RunConfiguration;
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env)
        throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();
        ExecutionResult executionResult = state.execute(env.getExecutor(), this);
        return showRunContent(executionResult, env, ((Perl6ProfileCommandLineState)state));
    }

    private static RunContentDescriptor showRunContent(ExecutionResult result, ExecutionEnvironment env, Perl6ProfileCommandLineState state)
        throws ExecutionException {
        return result != null
               ? new ProfileContentBuilder(result, env).showRunContent(env.getContentToReuse(), state)
               : null;
    }
}
