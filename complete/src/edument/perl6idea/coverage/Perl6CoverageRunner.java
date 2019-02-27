package edument.perl6idea.coverage;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.DefaultProgramRunner;
import edument.perl6idea.run.Perl6RunConfiguration;
import edument.perl6idea.testing.Perl6TestRunConfiguration;
import org.jetbrains.annotations.NotNull;

public class Perl6CoverageRunner extends DefaultProgramRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "Perl6 Coverage";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return CoverageExecutor.EXECUTOR_ID.equals(executorId) &&
               (profile instanceof Perl6RunConfiguration || profile instanceof Perl6TestRunConfiguration);
    }
}
