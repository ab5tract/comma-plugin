package edument.perl6idea.debugger;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promises;

public abstract class Perl6DefaultRunner implements ProgramRunner<RunnerSettings> {
    @Override
    public void execute(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        RunProfileState state = environment.getState();
        if (state != null) {
            ExecutionManager.getInstance(environment.getProject()).startRunProfile(environment, () -> {
                try {
                    return Promises.resolvedPromise(doExecute(state, environment));
                }
                catch (ExecutionException e) {
                    return null;
                }
            });
        }
    }

    protected RunContentDescriptor doExecute(RunProfileState state, ExecutionEnvironment env) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();
        ExecutionResult execResult = state.execute(env.getExecutor(), this);
        if (execResult != null) {
            return new RunContentBuilder(execResult, env).showRunContent(env.getContentToReuse());
        }
        else {
            return null;
        }
    }
}
