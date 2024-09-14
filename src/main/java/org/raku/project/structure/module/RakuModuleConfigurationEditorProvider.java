package org.raku.project.structure.module;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModuleRootModel;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.raku.module.RakuModuleType;

import java.util.ArrayList;
import java.util.List;

@InternalIgnoreDependencyViolation
public class RakuModuleConfigurationEditorProvider implements ModuleConfigurationEditorProvider {
    @Override
    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state) {
        ModuleRootModel rootModel = state.getCurrentRootModel();
        Module module = rootModel.getModule();
        if (!(ModuleType.get(module) instanceof RakuModuleType)) {
            return ModuleConfigurationEditor.EMPTY;
        } else {
            List<ModuleConfigurationEditor> editors = new ArrayList<>();
            editors.add(new ModuleMetaEditor(state));
            editors.add(new RakuModuleDependenciesEditor(state));
            return editors.toArray(ModuleConfigurationEditor.EMPTY);
        }
    }
}
