package org.raku.profiler.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.profiler.model.RakuProfileData;
import org.raku.run.RakuRunCommandLineState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

public class RakuProfileCommandLineState extends RakuRunCommandLineState {
    static final Logger LOG = Logger.getInstance(RakuProfileCommandLineState.class);
    private RakuProfileData[] profileData = new RakuProfileData[0];
    @Nullable
    private VirtualFile resultsFile;
    @Nullable
    private File tempFile = null;

    public RakuProfileCommandLineState(ExecutionEnvironment environment) {
        super(environment);
    }

    public RakuProfileCommandLineState(ExecutionEnvironment environment, VirtualFile resultsFile) {
        super(environment);
        this.resultsFile = resultsFile;
        this.tempFile = Paths.get(resultsFile.getPath()).toFile();
    }

    public RakuProfileCommandLineState(ExecutionEnvironment environment, RakuProfileData[] profileData) {
        super(environment);
        this.profileData = profileData;
    }

    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {
        if (resultsFile == null && profileData == null)
            return super.startProcess();
        else
            return new ProcessHandler() {
                @Override
                protected void destroyProcessImpl() {}

                @Override
                protected void detachProcessImpl() {}

                @Override
                public boolean detachIsDefault() {
                    return false;
                }

                @Override
                public @Nullable OutputStream getProcessInput() {
                    return null;
                }
            };
    }

    @Override
    protected void populateRunCommand() throws ExecutionException {
        if (resultsFile != null || profileData != null) {
            return;
        }

        String canonicalPath;
        try {
            tempFile = FileUtil.createTempFile("comma-profiler", ".sql");
            // We use safe canonical path here, as running profiler is not performance-critical operation
            canonicalPath = tempFile.getCanonicalPath();
        }
        catch (IOException e) {
            LOG.warn(e);
            throw new ExecutionException(e.getMessage());
        }
        command.add(String.format("--profile=%s", canonicalPath));
        setInterpreterParameters();
    }

    @Nullable
    public File getProfileResultsFile() {
        return tempFile;
    }

    public boolean hasFile() {
        return resultsFile != null;
    }

    public RakuProfileData[] getProfilerResultData() {
        return profileData;
    }

    public boolean hasData() {
        return profileData.length > 0;
    }
}
