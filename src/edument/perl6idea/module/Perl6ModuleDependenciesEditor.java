package edument.perl6idea.module;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.openapi.roots.ui.configuration.ModuleElementsEditor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Perl6ModuleDependenciesEditor extends ModuleElementsEditor implements ModuleRootListener {
    private Perl6DependenciesPanelImpl myPanel;

    protected Perl6ModuleDependenciesEditor(@NotNull ModuleConfigurationState state) {
        super(state);
    }

    @Override
    public void saveData() {
        myPanel.stopEditing();
    }

    @Override
    protected JComponent createComponentImpl() {
        if (myPanel == null)
            myPanel = new Perl6DependenciesPanelImpl(getState(), getModel().getProject());
        return myPanel;
    }

    @Override
    public void apply() throws ConfigurationException {
        JSONObject meta = Perl6ModuleBuilder.getMetaJsonFromModulePath(getState().getRootModel().getModule().getModuleFilePath());
        if (meta == null) throw new ConfigurationException("META6.json file does not exist");
        JSONArray depends       = new JSONArray();
        JSONArray test_depends  = new JSONArray();
        JSONArray build_depends = new JSONArray();
        for (Perl6DependencyTableItem item : myPanel.getModel().getItems()) {
            switch (item.getScope()) {
                case DEPENDS:
                    depends.put(item.getEntry()); break;
                case TEST_DEPENDS:
                    test_depends.put(item.getEntry()); break;
                case BUILD_DEPENDS:
                    build_depends.put(item.getEntry()); break;
            }
        }
        meta.put("depends", depends);
        meta.put("test-depends", test_depends);
        meta.put("build-depends", build_depends);
        Path path = Paths.get(getState().getRootModel().getModule().getModuleFilePath())
                         .resolveSibling("META6.json");
        Perl6ModuleBuilder.writeMetaFile(path, meta);
        myPanel.getModel().saveState();
    }

    public boolean isModified() {
        return myPanel.isModified();
    }

    @Override
    public void moduleStateChanged() {
        if (myPanel != null) myPanel.initFromModel();
    }

    @Override
    public void rootsChanged(ModuleRootEvent event) {
        if (myPanel != null) myPanel.rootsChanged();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Dependencies";
    }
}
