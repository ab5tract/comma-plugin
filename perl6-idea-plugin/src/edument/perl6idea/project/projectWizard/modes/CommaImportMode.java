package edument.perl6idea.project.projectWizard.modes;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import edument.perl6idea.project.Perl6ProjectImportProvider;
import edument.perl6idea.project.projectWizard.StepSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommaImportMode extends CommaWizardMode {
    private Perl6ProjectImportProvider myProvider;

    public CommaImportMode(Perl6ProjectImportProvider provider) {
        myProvider = provider;
    }

    @Override
    protected @Nullable StepSequence createSteps(@NotNull WizardContext context, @NotNull ModulesProvider modulesProvider) {
        StepSequence stepSequence = new StepSequence();
        myProvider.addSteps(stepSequence, context, myProvider.getId());
        stepSequence.setType(myProvider.getId());
        return stepSequence;
    }
}
