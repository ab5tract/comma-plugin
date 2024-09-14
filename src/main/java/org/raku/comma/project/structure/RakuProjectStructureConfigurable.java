package org.raku.comma.project.structure;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.MasterDetailsComponent;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ex.IdeFocusTraversalPolicy;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.panels.Wrapper;
import com.intellij.ui.navigation.BackAction;
import com.intellij.ui.navigation.ForwardAction;
import com.intellij.ui.navigation.History;
import com.intellij.ui.navigation.Place;
import com.intellij.util.io.storage.HeavyProcessLatch;
import com.intellij.util.ui.UIUtil;
import org.raku.comma.project.structure.ui.SidePanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RakuProjectStructureConfigurable extends BaseConfigurable implements Disposable,
                                                                                  SearchableConfigurable,
                                                                                  Place.Navigator, Configurable.NoMargin,
                                                                                  Configurable.NoScroll
{
    private static final String CATEGORY = "category";
    public static final DataKey<RakuProjectStructureConfigurable> KEY = DataKey.create("RakuProjectStructureConfiguration");
    protected final RakuProjectStructureConfigurable.UIState myUiState = new RakuProjectStructureConfigurable.UIState();
    private final Project myProject;
    private final RakuStructureConfigurableContext myContext;
    private JPanel myComponent;
    private OnePixelSplitter mySplitter;
    private SidePanel mySidePanel;
    private final ProjectSdksModel myProjectSdkModel = new ProjectSdksModel();
    // Configurables
    private RakuModuleStructureConfigurable myModulesConfigurable;
    private RakuProjectConfigurable myProjectConfigurable;
    private final List<Configurable> myName2Config = new ArrayList<>();
    private final Wrapper myDetails = new Wrapper();
    private boolean myUiInitialized;
    private Configurable mySelectedConfigurable;
    private final History myHistory = new History(this);
    private JComponent myToFocus;
    private final JLabel myEmptySelection =
        new JLabel("<html><body><center>Select a setting to view or edit its details here</center></body></html>",
                   SwingConstants.CENTER);
    private RakuSdkListConfigurable myJdkListConfig;

    public RakuModuleStructureConfigurable getModulesConfig() {
        return myModulesConfigurable;
    }

    public RakuStructureConfigurableContext getContext() {
        return myContext;
    }

    public ActionCallback select(@Nullable final String moduleToSelect, @Nullable String editorNameToSelect, final boolean requestFocus) {
        Place place = createModulesPlace();
        if (moduleToSelect != null) {
            final Module module = ModuleManager.getInstance(myProject).findModuleByName(moduleToSelect);
            assert module != null;
            place.putPath(MasterDetailsComponent.TREE_OBJECT, module).putPath(ModuleEditor.SELECTED_EDITOR_NAME, editorNameToSelect);
        }
        return navigateTo(place, requestFocus);
    }

    public Place createModulesPlace() {
        return createPlaceFor(myModulesConfigurable);
    }

    public ActionCallback select(@NotNull Sdk sdk, final boolean requestFocus) {
        Place place = createPlaceFor(myJdkListConfig);
        place.putPath(MasterDetailsComponent.TREE_NAME, sdk.getName());
        return navigateTo(place, requestFocus);
    }

    public static class UIState {
        public float proportion;
        public float sideProportion;

        public String lastEditedConfigurable;
    }

    public RakuProjectStructureConfigurable(final Project project) {
        myProject = project;
        RakuModulesConfigurator moduleConfigurator = new RakuModulesConfigurator(myProject);
        myContext = new RakuStructureConfigurableContext(moduleConfigurator);
        moduleConfigurator.setContext(myContext);

        myModulesConfigurable = new RakuModuleStructureConfigurable(project, myContext);
        myModulesConfigurable.init(myContext);

        final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(myProject);
        final String proportion = propertiesComponent.getValue("project.structure.proportion");
        myUiState.proportion = proportion != null ? Float.parseFloat(proportion) : 0;
        final String sideProportion = propertiesComponent.getValue("project.structure.side.proportion");
        myUiState.sideProportion = sideProportion != null ? Float.parseFloat(sideProportion) : 0;
    }

    @NotNull
    @Override
    public String getId() {
        return "raku.project.structure";
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Raku Project Structure";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        myComponent = new MyComponentPanel();

        mySplitter = new OnePixelSplitter(false, .15f);
        mySplitter.setSplitterProportionKey("Raku.ProjectStructure.TopLevelElements");
        mySplitter.setHonorComponentsMinimumSize(true);
        mySplitter.setFirstComponent(createLeftBar());
        mySplitter.setSecondComponent(myDetails);

        myComponent.add(mySplitter, BorderLayout.CENTER);

        navigateTo(createPlaceFor(myProjectConfigurable), true);
        myUiInitialized = true;

        return myComponent;
    }

    @NotNull
    private JPanel createLeftBar() {
        final JPanel left = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getMinimumSize() {
                final Dimension original = super.getMinimumSize();
                return new Dimension(Math.max(original.width, 200), original.height);
            }
        };

        final DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.add(new BackAction(myComponent, this));
        toolbarGroup.add(new ForwardAction(myComponent, this));
        final ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("Raku.ProjectStructure", toolbarGroup, true);
        toolbar.setTargetComponent(myComponent);
        JComponent toolbarComponent = toolbar.getComponent();
        left.setBackground(UIUtil.SIDE_PANEL_BACKGROUND);
        toolbarComponent.setBackground(UIUtil.SIDE_PANEL_BACKGROUND);
        left.add(toolbarComponent, BorderLayout.NORTH);

        initSidePanel();
        left.add(mySidePanel, BorderLayout.CENTER);
        return left;
    }

    private void initSidePanel() {
        mySidePanel = new SidePanel(this);
        mySidePanel.addSeparator("Project Settings");
        addProjectConfigurable();
        addModulesConfigurable();
        mySidePanel.addSeparator("Platform Settings");
        addSdkListConfig();
    }

    private void addModulesConfigurable() {
        myModulesConfigurable = new RakuModuleStructureConfigurable(myProject, myContext);
        myModulesConfigurable.init(myContext);
        addConfigurable(myModulesConfigurable);
    }

    private void addProjectConfigurable() {
        myProjectConfigurable = new RakuProjectConfigurable(myProject, myProjectSdkModel);
        addConfigurable(myProjectConfigurable);
    }

    private void addSdkListConfig() {
        myJdkListConfig = new RakuSdkListConfigurable(myProject);
        myJdkListConfig.init(myContext);
        addConfigurable(myJdkListConfig);
    }

    private void addConfigurable(Configurable configurable) {
        myName2Config.add(configurable);
        mySidePanel.addPlace(createPlaceFor(configurable), new Presentation(configurable.getDisplayName()));
    }

    public ProjectSdksModel getProjectSdksModel() {
        return myProjectSdkModel;
    }

    @Override
    public void dispose() {}

    private static Place createPlaceFor(Configurable configurable) {
        return new Place().putPath(CATEGORY, configurable);
    }

    @Override
    public void reset() {
        HeavyProcessLatch.INSTANCE.performOperation(
            HeavyProcessLatch.Type.Processing, "Resetting Raku Project Structure", () -> {
                myContext.reset();
                myProjectSdkModel.reset(myProject);
                Configurable toSelect = null;
                for (Configurable each : myName2Config) {
                    if (myUiState.lastEditedConfigurable != null && myUiState.lastEditedConfigurable.equals(each.getDisplayName())) {
                        toSelect = each;
                    }
                    if (each instanceof MasterDetailsComponent) {
                        ((MasterDetailsComponent)each).setHistory(myHistory);
                    }
                    each.reset();
                }

                myHistory.clear();

                if (toSelect == null && ! myName2Config.isEmpty()) {
                    toSelect = myName2Config.getFirst();
                }

                removeSelected();

                navigateTo(toSelect != null ? createPlaceFor(toSelect) : null, false);

                if (myUiState.proportion > 0) {
                    mySplitter.setProportion(myUiState.proportion);
                }
            });
    }

    @Override
    public boolean isModified() {
        for (Configurable each : myName2Config) {
            if (each.isModified()) return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        for (Configurable each : myName2Config) {
            if (each.isModified()) {
                each.apply();
            }
        }
    }

    @Override
    public ActionCallback navigateTo(@Nullable Place place, boolean requestFocus) {
        assert place != null;
        final Configurable toSelect = (Configurable) place.getPath(CATEGORY);
        JComponent detailsContent = myDetails.getTargetComponent();

        if (mySelectedConfigurable != toSelect) {
            saveSideProportion();
            removeSelected();

            if (toSelect != null) {
                detailsContent = toSelect.createComponent();
                myDetails.setContent(detailsContent);
                myUiState.lastEditedConfigurable = toSelect.getDisplayName();
            }

            mySelectedConfigurable = toSelect;

            if (toSelect instanceof final MasterDetailsComponent master) {
                if (myUiState.sideProportion > 0) {
                    master.getSplitter().setProportion(myUiState.sideProportion);
                }
                master.setHistory(myHistory);
            }
        }

        if (detailsContent != null) {
            JComponent toFocus = IdeFocusTraversalPolicy.getPreferredFocusedComponent(detailsContent);
            if (toFocus == null) {
                toFocus = detailsContent;
            }
            if (requestFocus) {
                myToFocus = toFocus;
                IdeFocusManager.findInstance().requestFocus(toFocus, true);
            }
        }

        myDetails.revalidate();
        myDetails.repaint();

        if (toSelect != null) mySidePanel.select(createPlaceFor(toSelect));
        if (!myHistory.isNavigatingNow() && mySelectedConfigurable != null) {
            myHistory.pushQueryPlace();
        }
        return ActionCallback.DONE;
    }

    private void removeSelected() {
        myDetails.removeAll();
        mySelectedConfigurable = null;
        myUiState.lastEditedConfigurable = null;
        myDetails.add(myEmptySelection, BorderLayout.CENTER);
    }

    private void saveSideProportion() {
        if (mySelectedConfigurable instanceof MasterDetailsComponent) {
            myUiState.sideProportion = ((MasterDetailsComponent)mySelectedConfigurable).getSplitter().getProportion();
        }
    }

    @Override
    public void disposeUIResources() {
        if (!myUiInitialized) return;
        final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(myProject);
        propertiesComponent.setValue("raku.project.structure.last.edited", myUiState.lastEditedConfigurable);
        propertiesComponent.setValue("raku.project.structure.proportion", String.valueOf(myUiState.proportion));
        propertiesComponent.setValue("raku.project.structure.side.proportion", String.valueOf(myUiState.sideProportion));

        myUiState.proportion = mySplitter.getProportion();
        saveSideProportion();
        myName2Config.forEach(Configurable::disposeUIResources);
        myContext.clear();
        myName2Config.clear();
        myUiInitialized = false;
    }

    public static RakuProjectStructureConfigurable getInstance(final Project project) {
        return project.getService(RakuProjectStructureConfigurable.class);
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return myToFocus;
    }

    protected class MyComponentPanel extends JPanel implements DataProvider {
        public MyComponentPanel() {
            super(new BorderLayout());
        }

        @Nullable
        @Override
        public Object getData(@NotNull String dataId) {
            if (KEY.is(dataId)) {
                return RakuProjectStructureConfigurable.this;
            }
            else if (History.KEY.is(dataId)) {
                return myHistory;
            }
            return null;
        }

        @Override
        public Dimension getMinimumSize() {
            final Dimension original = super.getMinimumSize();
            return new Dimension(Math.max(original.width, 750), original.height);
        }
    }
}
