package org.raku.comma.project.projectWizard.modes;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.raku.comma.project.RakuProjectImportProvider;
import org.raku.comma.project.projectWizard.StepSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommaImportMode extends CommaWizardMode {
    private final RakuProjectImportProvider myProvider;

    public CommaImportMode(RakuProjectImportProvider provider) {
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
