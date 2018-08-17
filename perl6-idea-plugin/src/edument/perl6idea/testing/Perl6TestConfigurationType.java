package edument.perl6idea.testing;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;

public class Perl6TestConfigurationType extends ConfigurationTypeBase implements DumbAware {
    private static final String PERL6_TEST_CONFIGURATION_ID = "PERL6_TEST_CONFIGURATION";

    protected Perl6TestConfigurationType() {
        super(PERL6_TEST_CONFIGURATION_ID, "Perl 6 test",
                "Run Perl 6 tests", Perl6Icons.CAMELIA);
        addFactory(new ConfigurationFactory(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new Perl6TestRunConfiguration(project, this, "Test project");
            }
        });
    }

    @NotNull
    public static Perl6TestConfigurationType getInstance() {
        return CONFIGURATION_TYPE_EP.findExtension(Perl6TestConfigurationType.class);
    }
}
