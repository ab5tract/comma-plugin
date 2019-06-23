package edument.perl6idea.project.structure.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import edument.perl6idea.module.Perl6ModuleType;

import java.util.ArrayList;
import java.util.List;

public class Perl6ModuleConfigurationEditorProvider implements ModuleConfigurationEditorProvider {
    @Override
    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state) {
        ModifiableRootModel rootModel = state.getRootModel();
        Module module = rootModel.getModule();
        if (!(ModuleType.get(module) instanceof Perl6ModuleType)) {
            return ModuleConfigurationEditor.EMPTY;
        } else {
            List<ModuleConfigurationEditor> editors = new ArrayList<>();
            editors.add(new ModuleMetaEditor(state));
            editors.add(new Perl6ModuleDependenciesEditor(state));
            return editors.toArray(ModuleConfigurationEditor.EMPTY);
        }
    }
}
