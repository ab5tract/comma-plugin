package edument.perl6idea.testing;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6CommunityTestConfigurationType extends ConfigurationTypeBase implements DumbAware {
    private static final String PERL6_TEST_CONFIGURATION_ID = "PERL6_TEST_CONFIGURATION";

    protected Perl6CommunityTestConfigurationType() {
        super(PERL6_TEST_CONFIGURATION_ID, "Raku test",
              "Run Raku tests", Perl6Icons.CAMELIA);
        addFactory(new ConfigurationFactory(this) {
            @Override
            public @NotNull String getId() {
                return PERL6_TEST_CONFIGURATION_ID + "_FACTORY";
            }

            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new Perl6CommunityTestRunConfiguration(project, this);
            }
        });
    }

    @NotNull
    public static Perl6CommunityTestConfigurationType getInstance() {
        return CONFIGURATION_TYPE_EP.findExtension(Perl6CommunityTestConfigurationType.class);
    }

    private static class Perl6CommunityTestRunConfiguration extends Perl6TestRunConfiguration {
        Perl6CommunityTestRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory) {
            super(project, factory);
        }

        @Nullable
        @Override
        public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
            return new Perl6TestRunningState(environment, executor instanceof DefaultDebugExecutor);
        }
    }
}
