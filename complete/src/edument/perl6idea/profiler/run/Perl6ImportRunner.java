package edument.perl6idea.profiler.run;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.profiler.model.Perl6ProfileData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6ImportRunner implements RunProfile {
    @Nullable
    private VirtualFile file;
    @Nullable
    private Perl6ProfileData data;

    public Perl6ImportRunner(@NotNull VirtualFile file) {
        this.file = file;
    }

    public Perl6ImportRunner(@NotNull Perl6ProfileData data) {
        this.data = data;
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        if (file != null)
            return new Perl6ProfileCommandLineState(environment, file);
        else if (data != null)
            return new Perl6ProfileCommandLineState(environment, data);
        return null;
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
