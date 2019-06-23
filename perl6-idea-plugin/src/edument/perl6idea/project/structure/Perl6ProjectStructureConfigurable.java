package edument.perl6idea.project.structure;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ProjectStructureConfigurable;
import com.intellij.openapi.roots.ui.configuration.ProjectStructureConfigurableFilter;
import com.intellij.openapi.roots.ui.configuration.SidePanel;
import com.intellij.openapi.roots.ui.configuration.projectRoot.*;
import com.intellij.openapi.ui.DetailsComponent;
import com.intellij.openapi.ui.MasterDetailsComponent;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.wm.ex.IdeFocusTraversalPolicy;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.panels.Wrapper;
import com.intellij.ui.navigation.BackAction;
import com.intellij.ui.navigation.ForwardAction;
import com.intellij.ui.navigation.History;
import com.intellij.ui.navigation.Place;
import com.intellij.util.io.storage.HeavyProcessLatch;
import com.intellij.util.ui.UIUtil;
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
    public static final DataKey<ProjectStructureConfigurable> KEY = DataKey.create("ProjectStructureConfiguration");
    protected final ProjectStructureConfigurable.UIState myUiState = new ProjectStructureConfigurable.UIState();
    private final Project myProject;
    private final Perl6ModulesConfigurator myModuleConfigurator;
    private ModuleStructureConfigurable myModulesConfig;
    private final StructureConfigurableContext myContext;
    private JPanel myComponent;
    private OnePixelSplitter mySplitter;
    private SidePanel mySidePanel;
    private final ProjectSdksModel myProjectSdkModel = new ProjectSdksModel();
    private Perl6ProjectConfigurable myProjectConfig;
    private final List<Configurable> myName2Config = new ArrayList<>();
    private JComponent myToolbarComponent;
    private Wrapper myDetails = new Wrapper();
    private boolean myUiInitialized;
    private Configurable mySelectedConfigurable;
    private History myHistory = new History(this);
    private JComponent myToFocus;
    private JLabel myEmptySelection = new JLabel("<html><body><center>Select a setting to view or edit its details here</center></body></html>",
                                                 SwingConstants.CENTER);

    public Perl6ProjectStructureConfigurable(final Project project,
                                             final ModuleStructureConfigurable moduleStructureConfigurable) {
        myProject = project;

        myModuleConfigurator = new Perl6ModulesConfigurator(myProject);
        myContext = new StructureConfigurableContext(myProject, myModuleConfigurator);
        myModuleConfigurator.setContext(myContext);

        myModulesConfig = moduleStructureConfigurable;
        myModulesConfig.init(myContext);

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
        return "Perl 6 Project Structure";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        myComponent = new MyComponentPanel();

        mySplitter = new OnePixelSplitter(false, .15f);
        mySplitter.setSplitterProportionKey("ProjectStructure.TopLevelElements");
        mySplitter.setHonorComponentsMinimumSize(true);
        mySplitter.setFirstComponent(createLeftBar());
        mySplitter.setSecondComponent(myDetails);

        myComponent.add(mySplitter, BorderLayout.CENTER);

        navigateTo(createPlaceFor(myProjectConfig), true);
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
        final ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("ProjectStructure", toolbarGroup, true);
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
        addProjectConfig();
        addModulesConfig();
    }

    private void addModulesConfig() {
        addConfigurable(myModulesConfig, ProjectStructureConfigurableFilter.ConfigurableId.MODULES);
    }

    private void addConfigurable(Configurable config, ProjectStructureConfigurableFilter.ConfigurableId configurableId) {
        addConfigurable(config, isAvailable(configurableId));
    }

    private void addConfigurable(Perl6ProjectConfigurable config, ProjectStructureConfigurableFilter.ConfigurableId configurableId) {
        addConfigurable(config, isAvailable(configurableId));
    }

    private void addProjectConfig() {
        myProjectConfig = new Perl6ProjectConfigurable(myProject, myContext, myProjectSdkModel);
        addConfigurable(myProjectConfig, ProjectStructureConfigurableFilter.ConfigurableId.PROJECT);
    }

    private void addConfigurable(Configurable configurable, boolean addToSidePanel) {
        myName2Config.add(configurable);
        if (addToSidePanel)
            mySidePanel.addPlace(createPlaceFor(configurable), new Presentation(configurable.getDisplayName()));
    }

    public ProjectSdksModel getProjectSdkModel() {
        return myProjectSdkModel;
    }

    public StructureConfigurableContext getContext() {
        return myContext;
    }

    @Override
    public void dispose() {}

    private static Place createPlaceFor(Configurable configurable) {
        return new Place().putPath(CATEGORY, configurable);
    }

    private boolean isAvailable(ProjectStructureConfigurableFilter.ConfigurableId id) {
        for (ProjectStructureConfigurableFilter filter : ProjectStructureConfigurableFilter.EP_NAME.getExtensions()) {
            if (!filter.isAvailable(id, myProject)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void reset() {
        AccessToken token = HeavyProcessLatch.INSTANCE.processStarted("Resetting Project Structure");
        try {
            myContext.reset();
            myProjectSdkModel.reset(myProject);
            myProjectConfig.reset();
            myModulesConfig.reset();
        } finally {
            token.finish();
        }
    }

    @Override
    public boolean isModified() {
        for (Configurable each : myName2Config)
            if (each.isModified()) return true;
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        LOG.assertTrue(TransactionGuard.getInstance().getContextTransaction() != null, "Project Structure should be shown in a transaction, see AnAction#startInTransaction");

        for (Configurable each : myName2Config)
            if (each.isModified())
                each.apply();
    }

    @Override
    public ActionCallback navigateTo(@Nullable Place place, boolean requestFocus) {
        final Configurable toSelect = (Configurable)place.getPath(CATEGORY);
        JComponent detailsContent = myDetails.getTargetComponent();

        if (mySelectedConfigurable != toSelect) {
            if (mySelectedConfigurable instanceof BaseStructureConfigurable)
                ((BaseStructureConfigurable)mySelectedConfigurable).onStructureUnselected();
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
                if (myUiState.sideProportion > 0)
                    master.getSplitter().setProportion(myUiState.sideProportion);
                master.setHistory(myHistory);
            }

            if (toSelect instanceof DetailsComponent.Facade) {
                DetailsComponent details = ((DetailsComponent.Facade)toSelect).getDetailsComponent();
                details.setBannerMinHeight(myToolbarComponent.getPreferredSize().height);
            }
            if (toSelect instanceof BaseStructureConfigurable)
                ((BaseStructureConfigurable)toSelect).onStructureSelected();
        }

        if (detailsContent != null) {
            JComponent toFocus = IdeFocusTraversalPolicy.getPreferredFocusedComponent(detailsContent);
            if (toFocus == null) toFocus = detailsContent;
            if (requestFocus) {
                myToFocus = toFocus;
                UIUtil.requestFocus(toFocus);
            }
        }

        final ActionCallback result = new ActionCallback();
        Place.goFurther(toSelect, place, requestFocus).notifyWhenDone(result);
        myDetails.revalidate();
        myDetails.repaint();

        if (toSelect != null) mySidePanel.select(createPlaceFor(toSelect));
        if (!myHistory.isNavigatingNow() && mySelectedConfigurable != null)
            myHistory.pushQueryPlace();
        return result;
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
        propertiesComponent.setValue("project.structure.last.edited", myUiState.lastEditedConfigurable);
        propertiesComponent.setValue("project.structure.proportion", String.valueOf(myUiState.proportion));
        propertiesComponent.setValue("project.structure.side.proportion", String.valueOf(myUiState.sideProportion));

        myUiState.proportion = mySplitter.getProportion();
        saveSideProportion();
        myContext.getDaemonAnalyzer().stop();
        for (Configurable each : myName2Config)
            each.disposeUIResources();
        myContext.clear();
        myName2Config.clear();

        myModuleConfigurator.getFacetsConfigurator().clearMaps();
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

    @Override
    public void queryPlace(@NotNull Place place) {
        place.putPath(CATEGORY, mySelectedConfigurable);
        Place.queryFurther(mySelectedConfigurable, place);
    }

    private class MyComponentPanel extends JPanel implements DataProvider {
        public MyComponentPanel() {
            super(new BorderLayout());
        }

        @Nullable
        @Override
        public Object getData(String dataId) {
            if (KEY.is(dataId)) {
                return Perl6ProjectStructureConfigurable.this;
            } else if (History.KEY.is(dataId)) {
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
