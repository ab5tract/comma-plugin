package edument.perl6idea.project.structure;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModuleEditor;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.roots.ui.configuration.projectRoot.daemon.ModuleProjectStructureElement;

public class Perl6ModulesConfigurator extends ModulesConfigurator {
    public Perl6ModulesConfigurator(Project project) {
        super(project);
    }

    @Override
    public void moduleRenamed(Module module, String oldName, String name) {
        ModuleEditor editor = getModuleEditor(module);
        if (editor != null) {
            editor.setModuleName(name);
            getContext().getDaemonAnalyzer().queueUpdate(
              new ModuleProjectStructureElement(getContext(), module)
            );
        }
    }
}
