package org.raku.cro.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import org.raku.RakuIcons;
import org.raku.coverage.RakuCoverageCommandLineState;
import org.raku.debugger.RakuDebugCommandLineState;
import org.raku.heapsnapshot.run.RakuHeapSnapshotCommandLineState;
import org.raku.profiler.run.RakuProfileCommandLineState;
import org.raku.run.RakuHeapSnapshotExecutor;
import org.raku.run.RakuProfileExecutor;
import org.raku.run.RakuRunCommandLineState;
import org.raku.timeline.RakuTimelineCommandLineState;
import org.raku.timeline.RakuTimelineExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RakuCompleteCroRunConfigurationType extends ConfigurationTypeBase {
    protected static final String PERL6_CRO_RUN_CONFIGURATION_ID = "PERL6_CRO_RUN_CONFIGURATION";

    protected RakuCompleteCroRunConfigurationType() {
        super(PERL6_CRO_RUN_CONFIGURATION_ID, "Cro Service", "Run Cro service", RakuIcons.CRO);
        addFactory(new ConfigurationFactory(this) {
            @Override
            public @NotNull String getId() {
                return "Cro Service";
            }

            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new RakuCroRunConfiguration(project, this, "Run Cro service");
            }
        });
    }

    @NotNull
    public static RakuCompleteCroRunConfigurationType getInstance() {
        return CONFIGURATION_TYPE_EP.findExtension(RakuCompleteCroRunConfigurationType.class);
    }

    private static class RakuCroRunConfiguration extends RakuCroRunConfigurationBase {
        RakuCroRunConfiguration(Project project, ConfigurationFactory factory, String name) {
            super(project, factory, name);
            configureCro(project);
        }

        @Nullable
        @Override
        public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
            if (executor instanceof DefaultDebugExecutor) {
                return new RakuDebugCommandLineState(environment);
            }
            else if (executor instanceof RakuProfileExecutor) {
                return new RakuProfileCommandLineState(environment);
            }
            else if (executor instanceof RakuHeapSnapshotExecutor) {
                return new RakuHeapSnapshotCommandLineState(environment);
            }
            if (Objects.equals(executor.getClass().getSimpleName(), "CoverageExecutor")) {
                return new RakuCoverageCommandLineState(environment);
            }
            else if (executor instanceof RakuTimelineExecutor) {
                return new RakuTimelineCommandLineState(environment);
            }
            return new RakuRunCommandLineState(environment);
        }
    }
}
