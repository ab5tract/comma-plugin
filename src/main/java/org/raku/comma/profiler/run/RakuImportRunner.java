package org.raku.comma.profiler.run;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.comma.RakuIcons;
import org.raku.comma.profiler.model.RakuProfileData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class RakuImportRunner implements RunProfile {
    @Nullable
    private VirtualFile file;
    private RakuProfileData[] data;

    public RakuImportRunner(@NotNull VirtualFile file) {
        this.file = file;
    }

    public RakuImportRunner(@NotNull List<RakuProfileData> data) {
        this.data = data.toArray(new RakuProfileData[0]);
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        if (file != null)
            return new RakuProfileCommandLineState(environment, file);
        else if (data != null)
            return new RakuProfileCommandLineState(environment, data);
        return null;
    }

    @Override
    public @NlsSafe @NotNull String getName() {
        return "Profiler results";
    }

    @Override
    public Icon getIcon() {
        return RakuIcons.CAMELIA_STOPWATCH;
    }
}
