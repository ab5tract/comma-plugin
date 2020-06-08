package edument.perl6idea.project.structure;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;

public class Perl6StructureConfigurableContext implements Disposable {
    private final Project myProject;
    Perl6ModulesConfigurator myModulesConfigurator;

    public Perl6StructureConfigurableContext(Project project, Perl6ModulesConfigurator modulesConfigurator) {
        myProject = project;
        myModulesConfigurator = modulesConfigurator;
    }

    @Override
    public void dispose() {}

    public void reset() {
        myModulesConfigurator.resetModuleEditors();
    }

    public void clear() {}
}
