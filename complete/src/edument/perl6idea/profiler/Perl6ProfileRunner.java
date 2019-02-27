package edument.perl6idea.profiler;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.DefaultProgramRunner;
import edument.perl6idea.run.Perl6RunConfiguration;
import org.jetbrains.annotations.NotNull;

public class Perl6ProfileRunner extends DefaultProgramRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "Perl 6 Profiler";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return profile instanceof Perl6RunConfiguration;
    }
}
