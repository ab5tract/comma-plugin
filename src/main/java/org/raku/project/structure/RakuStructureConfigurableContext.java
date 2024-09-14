package org.raku.project.structure;

import com.intellij.openapi.Disposable;

public class RakuStructureConfigurableContext implements Disposable {
    RakuModulesConfigurator myModulesConfigurator;

    public RakuStructureConfigurableContext(RakuModulesConfigurator modulesConfigurator) {
        myModulesConfigurator = modulesConfigurator;
    }

    @Override
    public void dispose() {}

    public void reset() {
        myModulesConfigurator.resetModuleEditors();
    }

    public void clear() {}
}
