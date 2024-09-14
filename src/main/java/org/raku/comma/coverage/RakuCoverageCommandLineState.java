package org.raku.comma.coverage;

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
import org.raku.comma.run.RakuRunCommandLineState;
import org.raku.comma.run.RakuRunConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RakuCoverageCommandLineState extends RakuRunCommandLineState {
    private final File coverageDir;

    public RakuCoverageCommandLineState(ExecutionEnvironment environment) throws ExecutionException {
        super(environment);
        try {
            coverageDir = FileUtil.createTempDirectory("coverage", Integer.toString(this.hashCode()));
        }
        catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    protected void setEnvironment(GeneralCommandLine cmd) {
        Map<String, String> env = new HashMap<>(((RakuRunConfiguration)runConfiguration).getEnvs());
        env.put("MVM_SPESH_DISABLE", "1"); // Avoid MoarVM bug
        env.put("MVM_COVERAGE_LOG", coverageDir.getAbsolutePath() + "/coverage.%d");
        cmd.withEnvironment(env);
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        RakuCoverageCommandLineState state = this;
        ProcessHandler handler = super.startProcess();
        handler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                // We might end up in a situation where we have multiple files written
                // because we spawned subprocs. In that case, take the one with the latest
                // last modified time, since that should be the one that terminated latest.
                long maxModified = Long.MIN_VALUE;
                List<File> maybeBest = new ArrayList<>();
                for (File candidate : coverageDir.listFiles()) {
                    if (!candidate.getName().startsWith("coverage."))
                        continue;
                    long mod = candidate.lastModified();
                    if (mod >= maxModified) {
                        maxModified = mod;
                        maybeBest.add(candidate);
                    }
                }

                // Disambiguate by lowest PID, then we have a result.
                maybeBest.sort(Comparator.comparing(RakuCoverageCommandLineState::extractPid));
                if (maybeBest.size() > 0) {
                    RakuCoverageDataManager.getInstance(getEnvironment().getProject())
                        .addSuiteFromSingleCoverageFile(maybeBest.get(0), state);
                }
                else {
                    Notifications.Bus.notify(new Notification("raku.coverage.error", "Coverage Error",
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

    private static final Pattern digits = Pattern.compile("\\d+");

    private static Integer extractPid(File f) {
        Matcher matcher = digits.matcher(f.getName());
        return matcher.matches() ? Integer.parseInt((matcher.toMatchResult().toString())) : 0;
    }
}
