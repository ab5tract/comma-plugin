// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.raku.project.projectWizard.components;

import com.intellij.diagnostic.PluginException;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.internal.statistic.utils.PluginInfoDetectorKt;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.WebModuleTypeBase;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.ui.popup.ListItemDescriptorAdapter;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.ProjectTemplateEP;
import com.intellij.platform.ProjectTemplatesFactory;
import com.intellij.platform.templates.LocalArchivedTemplate;
import com.intellij.platform.templates.TemplateModuleBuilder;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.SingleSelectionModel;
import com.intellij.ui.TreeUIHelper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.popup.list.GroupedItemsListRenderer;
import com.intellij.util.PlatformUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.FactoryMap;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.raku.project.projectWizard.*;
import org.raku.project.projectWizard.categories.TemplateBasedCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.*;

/**
 * @author Dmitry Avdeev
 */
public class CommaProjectTypeStep extends ModuleWizardStep implements SettingsStep, Disposable {
    private static final Logger LOG = Logger.getInstance(CommaProjectTypeStep.class);

//    private static final ExtensionPointName<ProjectTemplateEP> TEMPLATE_EP = ExtensionPointName.create("com.intellij.projectTemplate");
    private static final String TEMPLATES_CARD = "templates card";
    private static final String FRAMEWORKS_CARD = "frameworks card";
    private static final String PROJECT_WIZARD_GROUP = "project.wizard.group";
    private final WizardContext myContext;
    private final CommaNewProjectWizard myWizard;
    private final ModulesProvider myModulesProvider;
    private final ModuleBuilder.ModuleConfigurationUpdater myConfigurationUpdater;
    private final Map<ProjectTemplate, ModuleBuilder> myBuilders = FactoryMap.create(key -> (ModuleBuilder) key.createModuleBuilder());
    private final Map<String, ModuleWizardStep> myCustomSteps = new HashMap<>();
    private final MultiMap<TemplatesGroup, ProjectTemplate> myTemplatesMap;
    private JPanel myPanel;
    private JPanel myOptionsPanel;
    private JBList<TemplatesGroup> myProjectTypeList;
    private ProjectTemplateList myTemplatesList;
    private JPanel myHeaderPanel;
    @Nullable
    private ModuleWizardStep mySettingsStep;
    private String myCurrentCard;
    private TemplatesGroup myLastSelectedGroup;

    public CommaProjectTypeStep(WizardContext context, CommaNewProjectWizard wizard, ModulesProvider modulesProvider) {
        myContext = context;
        myWizard = wizard;

        myTemplatesMap = MultiMap.createConcurrent();
        final List<TemplatesGroup> groups = fillTemplatesMap(context);
        LOG.debug("groups=" + groups);

        myProjectTypeList.setModel(new CollectionListModel<>(groups));
        myProjectTypeList.setSelectionModel(new SingleSelectionModel());
        myProjectTypeList.addListSelectionListener(__ -> updateSelection());
        myProjectTypeList.setCellRenderer(new TemplatesGroupGroupedItemsListRenderer(groups));

        TreeUIHelper.getInstance().installListSpeedSearch(myProjectTypeList);

        myModulesProvider = modulesProvider;
        myConfigurationUpdater = new ModuleBuilder.ModuleConfigurationUpdater() {
            @Override
            public void update(@NotNull Module module, @NotNull ModifiableRootModel rootModel) {

            }
        };

        myProjectTypeList.getSelectionModel().addListSelectionListener(__ -> projectTypeChanged());

        myTemplatesList.addListSelectionListener(__ -> updateSelection());

        for (TemplatesGroup templatesGroup : myTemplatesMap.keySet()) {
            ModuleBuilder builder = templatesGroup.getModuleBuilder();
            if (builder != null) {
                myWizard.getSequence().addStepsForBuilder(builder, context, modulesProvider);
            }
            for (ProjectTemplate template : myTemplatesMap.get(templatesGroup)) {
                myWizard.getSequence().addStepsForBuilder(myBuilders.get(template), context, modulesProvider);
            }
        }

        final String groupId = PropertiesComponent.getInstance().getValue(PROJECT_WIZARD_GROUP);
        LOG.debug("saved groupId=" + groupId);
        if (groupId != null) {
            TemplatesGroup group = ContainerUtil.find(groups, group1 -> groupId.equals(group1.getId()));
            if (group != null) {
                myProjectTypeList.setSelectedValue(group, true);
            }
        }
        if (myProjectTypeList.getSelectedValue() == null) {
            myProjectTypeList.setSelectedIndex(0);
        }
        myTemplatesList.restoreSelection();
    }

    private static ModuleType<?> getModuleType(TemplatesGroup group) {
        ModuleBuilder moduleBuilder = group.getModuleBuilder();
        return moduleBuilder == null ? null : moduleBuilder.getModuleType();
    }

    private static MultiMap<TemplatesGroup, ProjectTemplate> getTemplatesMap(WizardContext context) {
        ProjectTemplatesFactory[] factories = ProjectTemplatesFactory.EP_NAME.getExtensions();
        final MultiMap<TemplatesGroup, ProjectTemplate> groups = new MultiMap<>();
        for (ProjectTemplatesFactory factory : factories) {
            for (String group : factory.getGroups()) {
                ProjectTemplate[] templates = factory.createTemplates(group, context);
                List<ProjectTemplate> values = Arrays.asList(templates);
                if (!values.isEmpty()) {
                    Icon icon = factory.getGroupIcon(group);
                    String parentGroup = factory.getParentGroup(group);
                    TemplatesGroup templatesGroup =
                            new TemplatesGroup(group, null, icon, factory.getGroupWeight(group), parentGroup, group, null);
                    groups.putValues(templatesGroup, values);
                }
            }
        }
        return groups;
    }

    private boolean isFrameworksMode() {
        return FRAMEWORKS_CARD.equals(myCurrentCard) && getSelectedBuilder().equals(myContext.getProjectBuilder());
    }

    private List<TemplatesGroup> fillTemplatesMap(WizardContext context) {
        List<ModuleBuilder> builders = ModuleBuilder.getAllBuilders();
        if (!context.isCreatingNewProject()) {
            builders.removeIf(mb -> mb instanceof EmptyModuleBuilder);
        }
        Map<String, TemplatesGroup> groupMap = new HashMap<>();
        for (ModuleBuilder builder : builders) {
            BuilderBasedTemplate template = new BuilderBasedTemplate(builder);
            if (builder.isTemplate()) {
                TemplatesGroup group = groupMap.get(builder.getGroupName());
                if (Objects.isNull(group)) {
                    group = new TemplatesGroup(builder);
                }
                myTemplatesMap.putValue(group, template);
            } else {
                TemplatesGroup group = new TemplatesGroup(builder);
                groupMap.put(group.getName(), group);
                myTemplatesMap.put(group, new ArrayList<>());
            }
        }

        MultiMap<TemplatesGroup, ProjectTemplate> map = getTemplatesMap(context);
        myTemplatesMap.putAllValues(map);

        if (context.isCreatingNewProject()) {
            MultiMap<String, ProjectTemplate> localTemplates = loadLocalTemplates();
            for (TemplatesGroup group : myTemplatesMap.keySet()) {
                myTemplatesMap.putValues(group, localTemplates.get(group.getId()));
            }
        }

        List<TemplatesGroup> groups = new ArrayList<>(myTemplatesMap.keySet());

        // sorting by module type popularity
        final MultiMap<ModuleType<?>, TemplatesGroup> moduleTypes = new MultiMap<>();
        for (TemplatesGroup group : groups) {
            ModuleType<?> type = getModuleType(group);
            moduleTypes.putValue(type, group);
        }
        Collections.sort(groups, (o1, o2) -> {
            int i = o2.getWeight() - o1.getWeight();
            if (i != 0) return i;
            int i1 = moduleTypes.get(getModuleType(o2)).size() - moduleTypes.get(getModuleType(o1)).size();
            if (i1 != 0) return i1;
            return o1.compareTo(o2);
        });

        Set<String> groupNames = ContainerUtil.map2Set(groups, TemplatesGroup::getParentGroup);

        // move subgroups
        MultiMap<String, TemplatesGroup> subGroups = new MultiMap<>();
        for (ListIterator<TemplatesGroup> iterator = groups.listIterator(); iterator.hasNext(); ) {
            TemplatesGroup group = iterator.next();
            String parentGroup = group.getParentGroup();
            if (parentGroup != null &&
                    groupNames.contains(parentGroup) &&
                    !group.getName().equals(parentGroup) &&
                    groupMap.containsKey(parentGroup)) {
                subGroups.putValue(parentGroup, group);
                iterator.remove();
            }
        }
        for (ListIterator<TemplatesGroup> iterator = groups.listIterator(); iterator.hasNext(); ) {
            TemplatesGroup group = iterator.next();
            for (TemplatesGroup subGroup : subGroups.get(group.getName())) {
                iterator.add(subGroup);
            }
        }

        return groups;
    }

    @TestOnly
    @Nullable
    ModuleWizardStep getSettingsStep() {
        return mySettingsStep;
    }

    // new TemplatesGroup selected
    private void projectTypeChanged() {
        TemplatesGroup group = getSelectedGroup();
        if (group == null || group == myLastSelectedGroup) return;
        myLastSelectedGroup = group;
        PropertiesComponent.getInstance().setValue(PROJECT_WIZARD_GROUP, group.getId());
        if (LOG.isDebugEnabled()) {
            LOG.debug("projectTypeChanged: " + group.getId() + " " + DebugUtil.currentStackTrace());
        }
        ModuleBuilder groupModuleBuilder = group.getModuleBuilder();

        mySettingsStep = null;
        myHeaderPanel.removeAll();
        if (groupModuleBuilder != null && groupModuleBuilder.getModuleType() != null) {
            mySettingsStep = groupModuleBuilder.modifyProjectTypeStep(this);
        }

        if (groupModuleBuilder == null || groupModuleBuilder.isTemplateBased()) {
            showTemplates(group);
        } else if (!showCustomOptions(groupModuleBuilder)) {
            getSelectedBuilder().addModuleConfigurationUpdater(myConfigurationUpdater);
            showCard(FRAMEWORKS_CARD);
        }

        myHeaderPanel.setVisible(myHeaderPanel.getComponentCount() > 0);
        // align header labels
        List<JLabel> labels = UIUtil.findComponentsOfType(myHeaderPanel, JLabel.class);
        int width = 0;
        for (JLabel label : labels) {
            int width1 = label.getPreferredSize().width;
            width = Math.max(width, width1);
        }
        for (JLabel label : labels) {
            label.setPreferredSize(new Dimension(width, label.getPreferredSize().height));
        }
        myHeaderPanel.revalidate();
        myHeaderPanel.repaint();

        updateSelection();
    }

    private void showCard(String card) {
        ((CardLayout) myOptionsPanel.getLayout()).show(myOptionsPanel, card);
        myCurrentCard = card;
    }

    private void showTemplates(TemplatesGroup group) {
        Collection<ProjectTemplate> templates = myTemplatesMap.get(group);
        setTemplatesList(group, templates, false);
        showCard(TEMPLATES_CARD);
    }

    private void setTemplatesList(TemplatesGroup group, Collection<? extends ProjectTemplate> templates, boolean preserveSelection) {
        List<ProjectTemplate> list = new ArrayList<>(templates);
        ModuleBuilder moduleBuilder = group.getModuleBuilder();
        if (moduleBuilder != null && !(moduleBuilder instanceof TemplateModuleBuilder)) {
            list.add(0, new BuilderBasedTemplate(moduleBuilder));
        }
        myTemplatesList.setTemplates(list, preserveSelection);
    }

    private boolean showCustomOptions(@NotNull ModuleBuilder builder) {
        String card = builder.getBuilderId();
        if (!myCustomSteps.containsKey(card)) {
            ModuleWizardStep step = builder.getCustomOptionsStep(myContext, this);
            if (step == null) return false;
            step.updateStep();
            myCustomSteps.put(card, step);
            myOptionsPanel.add(step.getComponent(), card);
        }
        showCard(card);
        return true;
    }

    @TestOnly
    public ModuleWizardStep getFrameworksStep() {
        return getCustomStep();
    }

    @Nullable
    private ModuleWizardStep getCustomStep() {
        return myCustomSteps.get(myCurrentCard);
    }

    private TemplatesGroup getSelectedGroup() {
        return myProjectTypeList.getSelectedValue();
    }

    @Nullable
    private ProjectTemplate getSelectedTemplate() {
        return Objects.equals(myCurrentCard, TEMPLATES_CARD) ? myTemplatesList.getSelectedTemplate() : null;
    }

    private ModuleBuilder getSelectedBuilder() {
        ProjectTemplate template = getSelectedTemplate();
        if (template != null) {
            return myBuilders.get(template);
        }
        return getSelectedGroup().getModuleBuilder();
    }

    public Collection<ProjectTemplate> getAvailableTemplates() {
        if (!Objects.equals(myCurrentCard, FRAMEWORKS_CARD)) {
            return Collections.emptyList();
        } else {
            return myTemplatesMap.get(getSelectedGroup());
        }
    }

    @Override
    public JComponent getComponent() {
        return myPanel;
    }

    @Override
    public void updateDataModel() {
        ModuleBuilder builder = getSelectedBuilder();
        if (builder != null) {
            myWizard.getSequence().addStepsForBuilder(builder, myContext, myModulesProvider);
        }
        ModuleWizardStep step = getCustomStep();
        if (step != null) {
            step.updateDataModel();
        }
        if (mySettingsStep != null) {
            mySettingsStep.updateDataModel();
        }
    }

    @Override
    public boolean validate() throws ConfigurationException {
        if (mySettingsStep != null) {
            if (!mySettingsStep.validate()) return false;
        }
        ModuleWizardStep step = getCustomStep();
        if (step != null && !step.validate()) {
            return false;
        }
        return super.validate();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return myProjectTypeList;
    }

    @Override
    public void dispose() {
        myLastSelectedGroup = null;
        mySettingsStep = null;
        myTemplatesMap.clear();
        myBuilders.clear();
        myCustomSteps.clear();
    }

    @Override
    public void disposeUIResources() {
        Disposer.dispose(this);
    }

    private MultiMap<String, ProjectTemplate> loadLocalTemplates() {
        MultiMap<String, ProjectTemplate> map = MultiMap.createConcurrent();
//        TEMPLATE_EP.processWithPluginDescriptor((ep, pluginDescriptor) -> {
//            ClassLoader classLoader = pluginDescriptor.getClassLoader();
//            URL url = classLoader.getResource(StringUtil.trimStart(ep.templatePath, "/"));
//            if (url == null) {
//                LOG.error(
//                        new PluginException("Can't find resource for project template: " + ep.templatePath, pluginDescriptor.getPluginId()));
//                return null;
//            }
//
//            try {
//                LocalArchivedTemplate template = new LocalArchivedTemplate(url, classLoader);
//                if (ep.category) {
//                    TemplateBasedCategory category = new TemplateBasedCategory(template, ep.projectType);
//                    myTemplatesMap.putValue(new TemplatesGroup(category), template);
//                } else {
//                    map.putValue(ep.projectType, template);
//                }
//            } catch (Exception e) {
//                LOG.error(new PluginException("Error loading template from URL: " + ep.templatePath, e, pluginDescriptor.getPluginId()));
//            }
//
//            return null;
//        });
        return map;
    }

    private void updateSelection() {
        ProjectTemplate template = getSelectedTemplate();
        if (template != null) {
            myContext.setProjectTemplate(template);
        }

        ModuleBuilder builder = getSelectedBuilder();
        LOG.debug(String.format("builder=%s; template=%s; group=%s; groupIndex=%d", builder, template, getSelectedGroup(),
                myProjectTypeList.getMinSelectionIndex()));

        myContext.setProjectBuilder(builder);
        if (builder != null) {
            StepSequence sequence = myWizard.getSequence();
            sequence.setType(builder.getBuilderId());
            sequence.setIgnoredSteps(builder.getIgnoredSteps());
        }
        myWizard.updateWizardButtons();
    }

    @TestOnly
    public String availableTemplateGroupsToString() {
        ListModel<TemplatesGroup> model = myProjectTypeList.getModel();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < model.getSize(); i++) {
            if (! builder.isEmpty()) {
                builder.append(", ");
            }
            builder.append(model.getElementAt(i).getName());
        }
        return builder.toString();
    }

    @TestOnly
    public boolean setSelectedTemplate(@NotNull String group, @Nullable String name) {
        ListModel<TemplatesGroup> model = myProjectTypeList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            TemplatesGroup templatesGroup = model.getElementAt(i);
            if (group.equals(templatesGroup.getName())) {
                myProjectTypeList.setSelectedIndex(i);
                if (name == null) {
                    return getSelectedGroup().getName().equals(group);
                } else {
                    setTemplatesList(templatesGroup, myTemplatesMap.get(templatesGroup), false);
                    return myTemplatesList.setSelectedTemplate(name);
                }
            }
        }
        return false;
    }

    @Override
    public WizardContext getContext() {
        return myContext;
    }

    @Override
    public void addSettingsField(@NotNull String label, @NotNull JComponent field) {
        CommaProjectSettingsStep.addField(label, field, myHeaderPanel);
    }

    @Override
    public void addSettingsComponent(@NotNull JComponent component) {
    }

    @Override
    public void addExpertPanel(@NotNull JComponent panel) {

    }

    @Override
    public void addExpertField(@NotNull String label, @NotNull JComponent field) {

    }

    @Override
    public String getHelpId() {
        if (getCustomStep() != null && getCustomStep().getHelpId() != null) {
            return getCustomStep().getHelpId();
        }
        return myContext.isCreatingNewProject() ? "Project_Category_and_Options" : "Module_Category_and_Options";
    }

  private static class TemplatesGroupGroupedItemsListRenderer extends GroupedItemsListRenderer<TemplatesGroup> {
    public TemplatesGroupGroupedItemsListRenderer(java.util.List<TemplatesGroup> groups) {
      super(new ListItemDescriptorAdapter<>() {
        @Nullable
        @Override
        public String getTextFor(TemplatesGroup value) {
          return value.getName();
        }

        @Nullable
        @Override
        public String getTooltipFor(TemplatesGroup value) {
          return value.getDescription();
        }

        @Nullable
        @Override
        public Icon getIconFor(TemplatesGroup value) {
          return value.getIcon();
        }

        @Override
        public boolean hasSeparatorAboveOf(TemplatesGroup value) {
          int index = groups.indexOf(value);
          if (index < 1) return false;
          TemplatesGroup upper = groups.get(index - 1);
          if (upper.getParentGroup() == null && value.getParentGroup() == null) return true;
          return !Objects.equals(upper.getParentGroup(), value.getParentGroup()) &&
                 !Objects.equals(upper.getName(), value.getParentGroup());
        }
      });
    }

    @Override
    protected JComponent createItemComponent() {
        JComponent component = super.createItemComponent();
        myTextLabel.setBorder(JBUI.Borders.empty(3));
        return component;
    }
  }
}
