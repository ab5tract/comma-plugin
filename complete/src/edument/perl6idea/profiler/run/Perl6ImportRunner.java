package edument.perl6idea.profiler.run;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6ImportRunner implements RunProfile {
    private final VirtualFile file;

    public Perl6ImportRunner(VirtualFile file) {
        this.file = file;
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return new Perl6ProfileCommandLineState(environment, file);
    }

    @Override
    public @NlsSafe @NotNull String getName() {
        return "Profiler results";
    }

    @Override
    public Icon getIcon() {
        return Perl6Icons.CAMELIA_STOPWATCH;
    }
}
