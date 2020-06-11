package edument.perl6idea.testing;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import edument.perl6idea.run.Perl6RunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6TestRunConfigurationProducer extends LazyRunConfigurationProducer<Perl6TestRunConfiguration> {
    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return new ConfigurationFactory() {
            @Override
            public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new CompletePerl6();
            }
        };
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull Perl6TestRunConfiguration configuration,
                                                    @NotNull ConfigurationContext context,
                                                    @NotNull Ref<PsiElement> sourceElement) {
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull Perl6TestRunConfiguration configuration, @NotNull ConfigurationContext context) {
        return false;
    }
}
