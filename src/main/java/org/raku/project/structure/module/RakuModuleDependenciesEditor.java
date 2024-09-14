package org.raku.project.structure.module;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.openapi.roots.ui.configuration.ModuleElementsEditor;
import com.intellij.openapi.util.text.StringUtil;
import org.raku.metadata.RakuMetaDataComponent;
import org.raku.project.structure.module.dependency.panel.RakuDependenciesPanelImpl;
import org.raku.project.structure.module.dependency.panel.RakuDependencyTableItem;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RakuModuleDependenciesEditor extends ModuleElementsEditor implements ModuleRootListener {
    private RakuDependenciesPanelImpl myPanel;

    protected RakuModuleDependenciesEditor(@NotNull ModuleConfigurationState state) {
        super(state);
    }

    @Override
    public void saveData() {
        myPanel.stopEditing();
    }

    @Override
    protected JComponent createComponentImpl() {
        if (myPanel == null)
            myPanel = new RakuDependenciesPanelImpl(getState(), getModel().getProject());
        return myPanel;
    }

    @Override
    public void apply() throws ConfigurationException {
        RakuMetaDataComponent metaData = getState().getCurrentRootModel().getModule().getService(RakuMetaDataComponent.class);
        if (!metaData.isMetaDataExist()) {
            try {
                metaData.createStubMetaFile(
                    StringUtil.sanitizeJavaIdentifier(getState().getProject().getName()),
                    null, false);
            }
            catch (IOException e) {
                throw new ConfigurationException("Cannot create META6.json file");
            }
        }
        List<String> depends      = new ArrayList<>();
        List<String> testDepends  = new ArrayList<>();
        List<String> buildDepends = new ArrayList<>();
        for (RakuDependencyTableItem item : myPanel.getModel().getItems()) {
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

    @Override
    public boolean isModified() {
        return myPanel.isModified();
    }

    @Override
    public void moduleStateChanged() {
        if (myPanel != null) myPanel.initFromModel();
    }

    @Override
    public void rootsChanged(@NotNull ModuleRootEvent event) {
        if (myPanel != null) myPanel.rootsChanged();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Dependencies";
    }
}
