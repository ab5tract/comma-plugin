package edument.perl6idea.project.structure;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
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
import edument.perl6idea.project.structure.ui.SidePanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Perl6ProjectStructureConfigurable extends BaseConfigurable implements Disposable,
                                                                                   SearchableConfigurable,
                                                                                   Place.Navigator, Configurable.NoMargin,
                                                                                   Configurable.NoScroll {
    private static final Logger LOG = Logger.getInstance(Perl6ProjectStructureConfigurable.class);
    private static final String CATEGORY = "category";
    public static final DataKey<Perl6ProjectStructureConfigurable> KEY = DataKey.create("Perl6ProjectStructureConfiguration");
    protected final Perl6ProjectStructureConfigurable.UIState myUiState = new Perl6ProjectStructureConfigurable.UIState();
    private Project myProject;
    private Perl6ModulesConfigurator myModuleConfigurator;
    private Perl6StructureConfigurableContext myContext;
    private JPanel myComponent;
    private OnePixelSplitter mySplitter;
    private SidePanel mySidePanel;
    private ProjectSdksModel myProjectSdkModel = new ProjectSdksModel();
    // Configurables
    private Perl6ModuleStructureConfigurable myModulesConfigurable;
    private Perl6ProjectConfigurable myProjectConfigurable;
    private final List<Configurable> myName2Config = new ArrayList<>();
    private JComponent myToolbarComponent;
    private Wrapper myDetails = new Wrapper();
    private boolean myUiInitialized;
    private Configurable mySelectedConfigurable;
    private History myHistory = new History(this);
    private JComponent myToFocus;
    private JLabel myEmptySelection =
        new JLabel("<html><body><center>Select a setting to view or edit its details here</center></body></html>",
                   SwingConstants.CENTER);

    public Perl6ModuleStructureConfigurable getModulesConfig() {
        return myModulesConfigurable;
    }

    public Perl6StructureConfigurableContext getContext() {
        return myContext;
    }

    public ActionCallback select(@NotNull Sdk sdk, final boolean requestFocus) {
        return null;
        // TODO tie to SDK tab when available
        //Place place = createPlaceFor(myJdkListConfig);
        //place.putPath(MasterDetailsComponent.TREE_NAME, sdk.getName());
        //return navigateTo(place, requestFocus);
    }

    public static class UIState {
        public float proportion;
        public float sideProportion;

        public String lastEditedConfigurable;
    }

    public Perl6ProjectStructureConfigurable(final Project project) {
        myProject = project;
        myModuleConfigurator = new Perl6ModulesConfigurator(myProject);
        myContext = new Perl6StructureConfigurableContext(myProject, myModuleConfigurator);
        myModuleConfigurator.setContext(myContext);

        myModulesConfigurable = new Perl6ModuleStructureConfigurable(project, myContext);
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
        return "perl6.project.structure";
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
        myToolbarComponent = toolbar.getComponent();
        left.setBackground(UIUtil.SIDE_PANEL_BACKGROUND);
        myToolbarComponent.setBackground(UIUtil.SIDE_PANEL_BACKGROUND);
        left.add(myToolbarComponent, BorderLayout.NORTH);

        initSidePanel();
        left.add(mySidePanel, BorderLayout.CENTER);
        return left;
    }

    private void initSidePanel() {
        mySidePanel = new SidePanel(this);
        mySidePanel.addSeparator("Project Settings");
        addProjectConfigurable();
        addModulesConfigurable();
    }

    private void addModulesConfigurable() {
        myModulesConfigurable = new Perl6ModuleStructureConfigurable(myProject, myContext);
        myModulesConfigurable.init(myContext);
        addConfigurable(myModulesConfigurable);
    }

    private void addProjectConfigurable() {
        myProjectConfigurable = new Perl6ProjectConfigurable(myProject, myContext, myProjectSdkModel);
        addConfigurable(myProjectConfigurable);
    }

    private void addConfigurable(Configurable configurable) {
        myName2Config.add(configurable);
        mySidePanel.addPlace(createPlaceFor(configurable), new Presentation(configurable.getDisplayName()));
    }

    // TODO SDK tab
    //public JdkListConfigurable getJdkConfig() {
    //    return myJdkListConfig;
    //}

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
        AccessToken token = HeavyProcessLatch.INSTANCE.processStarted("Resetting Raku Project Structure");
        try {
            myContext.reset();
            myProjectSdkModel.reset(myProject);
            myProjectConfigurable.reset();
            myModulesConfigurable.reset();
        }
        finally {
            token.finish();
        }
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
        final Configurable toSelect = (Configurable)place.getPath(CATEGORY);
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

            if (toSelect instanceof MasterDetailsComponent) {
                final MasterDetailsComponent master = (MasterDetailsComponent)toSelect;
                if (myUiState.sideProportion > 0) {
                    master.getSplitter().setProportion(myUiState.sideProportion);
                }
                master.setHistory(myHistory);
            }
        }

        if (detailsContent != null) {
            JComponent toFocus = IdeFocusTraversalPolicy.getPreferredFocusedComponent(detailsContent);
            if (toFocus == null) toFocus = detailsContent;
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
        for (Configurable each : myName2Config) {
            each.disposeUIResources();
        }
        myContext.clear();
        myName2Config.clear();
        myModuleConfigurator.disposeUIResources();
        myUiInitialized = false;
    }

    public static Perl6ProjectStructureConfigurable getInstance(final Project project) {
        return ServiceManager.getService(project, Perl6ProjectStructureConfigurable.class);
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
                return Perl6ProjectStructureConfigurable.this;
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
