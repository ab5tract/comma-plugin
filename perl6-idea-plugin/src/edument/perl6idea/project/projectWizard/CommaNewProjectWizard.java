package edument.perl6idea.project.projectWizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Disposer;
import edument.perl6idea.project.projectWizard.components.CommaChooseTemplateStep;
import edument.perl6idea.project.projectWizard.components.CommaProjectSettingsStep;
import edument.perl6idea.project.projectWizard.components.CommaProjectTypeStep;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class CommaNewProjectWizard extends CommaAbstractProjectWizard {
    protected final StepSequence mySequence = new StepSequence();

    public CommaNewProjectWizard(@Nullable Project project, @NotNull ModulesProvider modulesProvider, @Nullable String defaultPath) {
        super(project == null ? "New Project" : "New Module", project, defaultPath);
        init(modulesProvider);
    }

    // XXX Will be used by our configurator later
    public CommaNewProjectWizard(Project project, Component dialogParent, ModulesProvider modulesProvider, String defaultModuleName) {
        super("New Module", project, dialogParent);
        myWizardContext.setDefaultModuleName(defaultModuleName);
        init(modulesProvider);
    }

    protected void init(@NotNull ModulesProvider modulesProvider) {
        myWizardContext.setModulesProvider(modulesProvider);
        CommaProjectTypeStep projectTypeStep = new CommaProjectTypeStep(myWizardContext, this, modulesProvider);
        Disposer.register(getDisposable(), projectTypeStep);
        mySequence.addCommonStep(projectTypeStep);
        CommaChooseTemplateStep chooseTemplateStep = new CommaChooseTemplateStep(myWizardContext, projectTypeStep);
        mySequence.addCommonStep(chooseTemplateStep);
        mySequence.addCommonFinishingStep(new CommaProjectSettingsStep(myWizardContext), null);
        for (ModuleWizardStep step : mySequence.getAllSteps()) {
            addStep(step);
        }
        super.init();
    }

    @Override
    protected @Nullable String getDimensionServiceKey() {
        return "comma new project wizard";
    }

    @Override
    public StepSequence getSequence() {
        return mySequence;
    }
}
