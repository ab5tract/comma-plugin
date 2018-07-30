package edument.perl6idea.testing;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import edument.perl6idea.run.Perl6DebuggableConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6TestRunConfiguration extends RunConfigurationBase implements Perl6DebuggableConfiguration {
    Perl6TestRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new Perl6TestSettingsEditor(getProject());
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new Perl6TestRunningState(environment, executor instanceof DefaultDebugExecutor);
    }

    @Override
    public int getDebugPort() {
        return 9999;
    }

    @Override
    public void setDebugPort(int debugPort) {
    }

    @Override
    public boolean isStartSuspended() {
        return false;
    }

    @Override
    public void setStartSuspended(boolean startSuspended) {
    }
}