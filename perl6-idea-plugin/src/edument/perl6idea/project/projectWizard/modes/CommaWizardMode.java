package edument.perl6idea.project.projectWizard.modes;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import edument.perl6idea.project.projectWizard.StepSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommaWizardMode implements Disposable {
      private StepSequence myStepSequence;

    @Override
    public void dispose() {
        myStepSequence = null;
    }

    @Nullable
    public StepSequence getSteps(WizardContext context, ModulesProvider modulesProvider) {
        if (myStepSequence == null)
            myStepSequence = createSteps(context, modulesProvider);
        return myStepSequence;
    }

    @Nullable
    protected abstract StepSequence createSteps(@NotNull WizardContext context, @NotNull ModulesProvider modulesProvider);
}
