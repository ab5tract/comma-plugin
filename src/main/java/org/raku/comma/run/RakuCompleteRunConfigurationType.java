package org.raku.comma.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import org.raku.comma.RakuIcons;
import org.raku.comma.coverage.RakuCoverageCommandLineState;
import org.raku.comma.debugger.RakuDebugCommandLineState;
import org.raku.comma.heapsnapshot.run.RakuHeapSnapshotCommandLineState;
import org.raku.comma.profiler.run.RakuProfileCommandLineState;
import org.raku.comma.timeline.RakuTimelineCommandLineState;
import org.raku.comma.timeline.RakuTimelineExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RakuCompleteRunConfigurationType extends ConfigurationTypeBase {
    protected static final String RAKU_RUN_CONFIGURATION_ID = "RAKU_RUN_CONFIGURATION";

    protected RakuCompleteRunConfigurationType() {
        super(RAKU_RUN_CONFIGURATION_ID, "Raku", "Raku run configuration", RakuIcons.CAMELIA);
        addFactory(new ConfigurationFactory(this) {
            @Override
            public @NotNull String getId() {
                return "Raku";
            }

            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new RakuCompleteRunConfiguration(project, this, "Run script");
            }
        });
    }

    @NotNull
    public static RakuCompleteRunConfigurationType getInstance() {
        return Objects.requireNonNull(CONFIGURATION_TYPE_EP.findExtension(RakuCompleteRunConfigurationType.class));
    }

    private static class RakuCompleteRunConfiguration extends RakuRunConfiguration {
        public RakuCompleteRunConfiguration(Project project,
                                             ConfigurationFactory factory,
                                             String name)
        {
            super(project, factory, name);
        }

        @Nullable
        @Override
        public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
            if (executor.getClass().getSimpleName().equals("CoverageExecutor")) {
                return new RakuCoverageCommandLineState(environment);
            }

            return switch (executor) {
                case DefaultDebugExecutor x     -> new RakuDebugCommandLineState(environment);
                case RakuProfileExecutor x      -> new RakuProfileCommandLineState(environment);
                case RakuHeapSnapshotExecutor x -> new RakuHeapSnapshotCommandLineState(environment);
                case RakuTimelineExecutor x     -> new RakuTimelineCommandLineState(environment);
                default                         -> new RakuRunCommandLineState(environment);
            };
        }
    }
}
