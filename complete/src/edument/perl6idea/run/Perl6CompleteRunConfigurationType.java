package edument.perl6idea.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.coverage.Perl6CoverageCommandLineState;
import edument.perl6idea.debugger.Perl6DebugCommandLineState;
import edument.perl6idea.profiler.run.Perl6ProfileCommandLineState;
import edument.perl6idea.timeline.Perl6TimelineCommandLineState;
import edument.perl6idea.timeline.Perl6TimelineExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Perl6CompleteRunConfigurationType extends ConfigurationTypeBase {
    protected static final String PERL6_RUN_CONFIGURATION_ID = "PERL6_RUN_CONFIGURATION";

    protected Perl6CompleteRunConfigurationType() {
        super(PERL6_RUN_CONFIGURATION_ID, "Perl 6",
              "Run Perl 6 configuration", Perl6Icons.CAMELIA);
        addFactory(new ConfigurationFactory(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new Perl6CompleteRunConfiguration(project, this, "Run script");
            }
        });
    }

    @NotNull
    public static Perl6CompleteRunConfigurationType getInstance() {
        return CONFIGURATION_TYPE_EP.findExtension(Perl6CompleteRunConfigurationType.class);
    }

    private static class Perl6CompleteRunConfiguration extends Perl6RunConfiguration {
        public Perl6CompleteRunConfiguration(Project project,
                                             ConfigurationFactory factory,
                                             String name) {
            super(project, factory, name);
        }

        @Nullable
        @Override
        public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
            if (executor instanceof DefaultDebugExecutor) {
                return new Perl6DebugCommandLineState(environment);
            }
            else if (executor instanceof Perl6ProfileExecutor) {
                return new Perl6ProfileCommandLineState(environment);
            }
            if (Objects.equals(executor.getClass().getSimpleName(), "CoverageExecutor")) {
                return new Perl6CoverageCommandLineState(environment);
            }
            else if (executor instanceof Perl6TimelineExecutor) {
                return new Perl6TimelineCommandLineState(environment);
            }
            return new Perl6RunCommandLineState(environment);
        }
    }
}
