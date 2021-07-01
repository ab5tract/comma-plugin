package edument.perl6idea.project.structure;

import com.intellij.openapi.Disposable;

public class Perl6StructureConfigurableContext implements Disposable {
    Perl6ModulesConfigurator myModulesConfigurator;

    public Perl6StructureConfigurableContext(Perl6ModulesConfigurator modulesConfigurator) {
        myModulesConfigurator = modulesConfigurator;
    }

    @Override
    public void dispose() {}

    public void reset() {
        myModulesConfigurator.resetModuleEditors();
    }

    public void clear() {}
}
