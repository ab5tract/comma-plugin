package edument.perl6idea.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;

public class Perl6RunConfigurationType extends ConfigurationTypeBase {
    private static final String PERL6_RUN_CONFIGURATION_ID = "PERL6_RUN_CONFIGURATION";

    protected Perl6RunConfigurationType() {
        super(PERL6_RUN_CONFIGURATION_ID, "Perl 6",
              "Run Perl 6 configuration", Perl6Icons.CAMELIA);
        addFactory(new ConfigurationFactory(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new Perl6RunConfiguration(project, this, "Run script");
            }
        });
    }

    @NotNull
    public static Perl6RunConfigurationType getInstance() {
        return CONFIGURATION_TYPE_EP.findExtension(Perl6RunConfigurationType.class);
    }
}
