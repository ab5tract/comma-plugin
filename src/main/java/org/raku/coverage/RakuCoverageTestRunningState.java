package org.raku.coverage;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.util.io.FileUtil;
import org.raku.testing.RakuTestRunningState;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RakuCoverageTestRunningState extends RakuTestRunningState {
    private final File coverageDir;

    public RakuCoverageTestRunningState(ExecutionEnvironment environment) throws ExecutionException {
        super(environment);
        try {
          coverageDir = FileUtil.createTempDirectory("coverage", Integer.toString(this.hashCode()));
        }
        catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    protected GeneralCommandLine createCommandLine() throws ExecutionException {
      GeneralCommandLine cmd = super.createCommandLine();
      cmd.withEnvironment("COMMA_TEST_COVERAGE", coverageDir.getAbsolutePath());
      return cmd;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        RakuCoverageTestRunningState state = this;
        ProcessHandler handler = super.startProcess();
        handler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                File index = new File(coverageDir.getAbsolutePath() + File.separator + "index");
                if (index.exists()) {
                    RakuCoverageDataManager.getInstance(getEnvironment().getProject())
                            .addSuiteFromIndexFile(index, state);
                }
                else {
                    Notifications.Bus.notify(new Notification("raku.coverage.error", "Coverage error",
                            "No coverage data collected.", NotificationType.ERROR));
                }
                try {
                    Files.walk(Paths.get(coverageDir.getAbsolutePath()))
                        .map(Path::toFile)
                        .sorted((o1, o2) -> -FileUtil.compareFiles(o1, o2))
                        .forEach(File::delete);
                }
                catch (IOException ignored) {}
            }
        });
        return handler;
    }
}
