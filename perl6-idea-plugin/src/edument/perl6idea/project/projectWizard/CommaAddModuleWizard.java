package edument.perl6idea.project.projectWizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.DefaultModulesProvider;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import edument.perl6idea.project.Perl6ProjectBuilder;
import edument.perl6idea.project.Perl6ProjectImportProvider;
import edument.perl6idea.project.projectWizard.modes.CommaImportMode;
import edument.perl6idea.project.projectWizard.modes.CommaWizardMode;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class CommaAddModuleWizard extends CommaAbstractProjectWizard {
    private final Perl6ProjectImportProvider myImportProvider;
    private final ModulesProvider myModulesProvider;
    private CommaWizardMode myWizardMode;

    public CommaAddModuleWizard(@Nullable Project project, String filePath, Perl6ProjectImportProvider importProvider) {
        super(getImportWizardTitle(project, importProvider), project, filePath);
        this.myImportProvider = importProvider;
        myModulesProvider = DefaultModulesProvider.createForProject(project);
        initModuleWizard(project, filePath);
    }

    public CommaAddModuleWizard(Project project, Component dialogParent, String filePath, Perl6ProjectImportProvider importProvider) {
        super(getImportWizardTitle(project, importProvider), project, dialogParent);
        this.myImportProvider = importProvider;
        myModulesProvider = DefaultModulesProvider.createForProject(project);
        initModuleWizard(project, filePath);
    }

    private static String getImportWizardTitle(Project project, Perl6ProjectImportProvider provider) {
        return "Import " + (project == null ? "Project" : "Module") + " from " + provider.getName();
    }

    private void initModuleWizard(@Nullable final Project project, @Nullable final String defaultPath) {
        myWizardContext.addContextListener(new WizardContext.Listener() {
            @Override
            public void buttonsUpdateRequested() {
                updateButtons();
            }

            @Override
            public void nextStepRequested() {
                doNextAction();
            }
        });

        myWizardMode = new CommaImportMode(myImportProvider);
        StepSequence sequence = myWizardMode.getSteps(myWizardContext, DefaultModulesProvider.createForProject(project));
        appendSteps(sequence);
        myImportProvider.getBuilder().setFileToImport(defaultPath);
        Perl6ProjectBuilder builder = myImportProvider.getBuilder();
        myWizardContext.setProjectBuilder(builder);
        builder.setUpdate(myWizardContext.getProject() != null);
        init();
    }

    private void appendSteps(@Nullable final StepSequence sequence) {
        if (sequence != null) {
            for (ModuleWizardStep step : sequence.getAllSteps()) {
                addStep(step);
            }
        }
    }

    @Override
    public StepSequence getSequence() {
        return myWizardMode.getSteps(myWizardContext, myModulesProvider);
    }
}
