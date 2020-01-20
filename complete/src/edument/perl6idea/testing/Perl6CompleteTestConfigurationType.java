package edument.perl6idea.testing;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.coverage.Perl6CoverageTestRunningState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Perl6CompleteTestConfigurationType extends ConfigurationTypeBase implements DumbAware {
    private static final String PERL6_TEST_CONFIGURATION_ID = "PERL6_TEST_CONFIGURATION";

    protected Perl6CompleteTestConfigurationType() {
        super(PERL6_TEST_CONFIGURATION_ID, "Raku test",
              "Run Raku tests", Perl6Icons.CAMELIA);
        addFactory(new ConfigurationFactory(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new Perl6CompleteTestRunConfiguration(project, this, "Test project");
            }
        });
    }

    @NotNull
    public static Perl6CompleteTestConfigurationType getInstance() {
        return CONFIGURATION_TYPE_EP.findExtension(Perl6CompleteTestConfigurationType.class);
    }

    private static class Perl6CompleteTestRunConfiguration extends Perl6TestRunConfiguration {
        Perl6CompleteTestRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
            super(project, factory, name);
        }

        @Nullable
        @Override
        public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
            if (Objects.equals(executor.getClass().getSimpleName(), "CoverageExecutor")) {
                return new Perl6CoverageTestRunningState(environment);
            }
            return new Perl6TestRunningState(environment, executor instanceof DefaultDebugExecutor);
        }
    }
}
