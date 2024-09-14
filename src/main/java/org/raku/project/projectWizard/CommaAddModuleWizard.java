package org.raku.project.projectWizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.DefaultModulesProvider;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.raku.project.RakuProjectBuilder;
import org.raku.project.RakuProjectImportProvider;
import org.raku.project.projectWizard.modes.CommaImportMode;
import org.raku.project.projectWizard.modes.CommaWizardMode;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class CommaAddModuleWizard extends CommaAbstractProjectWizard {
    private final RakuProjectImportProvider myImportProvider;
    private final ModulesProvider myModulesProvider;
    private CommaWizardMode myWizardMode;

    public CommaAddModuleWizard(@Nullable Project project, String filePath, RakuProjectImportProvider importProvider) {
        super(getImportWizardTitle(project, importProvider), project, filePath);
        this.myImportProvider = importProvider;
        myModulesProvider = DefaultModulesProvider.createForProject(project);
        initModuleWizard(project, filePath);
    }

    public CommaAddModuleWizard(Project project, Component dialogParent, String filePath, RakuProjectImportProvider importProvider) {
        super(getImportWizardTitle(project, importProvider), project, dialogParent);
        this.myImportProvider = importProvider;
        myModulesProvider = DefaultModulesProvider.createForProject(project);
        initModuleWizard(project, filePath);
    }

    private static String getImportWizardTitle(Project project, RakuProjectImportProvider provider) {
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
        RakuProjectBuilder builder = myImportProvider.getBuilder();
        myWizardContext.setProjectBuilder(builder);
        myWizardContext.setProjectFileDirectory(defaultPath);
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
