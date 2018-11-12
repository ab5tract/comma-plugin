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
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
        Perl6MetaDataComponent metaData = getState().getRootModel().getModule().getComponent(Perl6MetaDataComponent.class);
        if (!metaData.isMetaDataExist()) {
            try {
                metaData.createStubMetaFile(null, false);
            }
            catch (IOException e) {
                throw new ConfigurationException("Cannot create META6.json file");
            }
        }
        List<String> depends      = new ArrayList<>();
        List<String> testDepends  = new ArrayList<>();
        List<String> buildDepends = new ArrayList<>();
        for (Perl6DependencyTableItem item : myPanel.getModel().getItems()) {
            switch (item.getScope()) {
                case DEPENDS:
                    depends.add(item.getEntry()); break;
                case TEST_DEPENDS:
                    testDepends.add(item.getEntry()); break;
                case BUILD_DEPENDS:
                    buildDepends.add(item.getEntry()); break;
            }
        }
        metaData.setDepends(depends);
        metaData.setTestDepends(testDepends);
        metaData.setBuildDepends(buildDepends);
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
