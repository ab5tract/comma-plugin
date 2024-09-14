package org.raku.comma.testing;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.raku.comma.RakuIcons;
import org.raku.comma.coverage.RakuCoverageTestRunningState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RakuCompleteTestConfigurationType extends ConfigurationTypeBase implements DumbAware {
    private static final String PERL6_TEST_CONFIGURATION_ID = "PERL6_TEST_CONFIGURATION";
    private final ConfigurationFactory myFactory = new RakuConfigurationFactory(this);

    protected RakuCompleteTestConfigurationType() {
        super(PERL6_TEST_CONFIGURATION_ID, "Raku test",
              "Run Raku tests", RakuIcons.CAMELIA);
        addFactory(new RakuConfigurationFactory(this));
    }

    ConfigurationFactory getFactory() {
        return myFactory;
    }

    public static RakuCompleteTestConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(RakuCompleteTestConfigurationType.class);
    }

    public static class RakuCompleteTestRunConfiguration extends RakuTestRunConfiguration {
        RakuCompleteTestRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory) {
            super(project, factory);
        }

        @Nullable
        @Override
        public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
            if (Objects.equals(executor.getClass().getSimpleName(), "CoverageExecutor")) {
                return new RakuCoverageTestRunningState(environment);
            }
            return new RakuTestRunningState(environment);
        }
    }

    public static class RakuConfigurationFactory extends ConfigurationFactory {
        protected RakuConfigurationFactory(ConfigurationType configurationType) {
            super(configurationType);
        }

        @Override
        @NotNull
        public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
            return new RakuCompleteTestRunConfiguration(project, this);
        }

        @Override
        public @NotNull String getId() {
            return "Raku test";
        }
    }
}
