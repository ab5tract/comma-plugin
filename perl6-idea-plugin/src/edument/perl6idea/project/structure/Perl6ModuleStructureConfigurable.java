package edument.perl6idea.project.structure;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.impl.ModuleGroupingTreeHelper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleGrouper;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NullableComputable;
import com.intellij.ui.navigation.Place;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Perl6ModuleStructureConfigurable extends Perl6StructureConfigurable implements Place.Navigator {
    private final ModuleManager myModuleManager;
    private final List<RemoveConfigurableHandler<?>> myRemoveHandlers;

    public Perl6ModuleStructureConfigurable(Project project, Perl6StructureConfigurableContext context) {
        super(project);
        myModuleManager = ModuleManager.getInstance(project);
        myContext = context;
        myRemoveHandlers = new ArrayList<>();
        myRemoveHandlers.add(new RakuDistRemoveHandler());
    }

    @Override
    protected void initTree() {
        super.initTree();
        myTree.setRootVisible(false);
    }

    @Override
    protected void loadTree() {
        createProjectNodes();
        getTreeModel().reload();
        myUiDisposed = false;
    }

    private DefaultTreeModel getTreeModel() {
        return (DefaultTreeModel)getTree().getModel();
    }

    private void createProjectNodes() {
        Module[] modules = myModuleManager.getModules();
        ModuleGrouper moduleGrouper = getModuleGrouper();
        ModuleGroupingTreeHelper<Module, MyNode> helper = ModuleGroupingTreeHelper.forEmptyTree(
            false, ModuleGroupingTreeHelper.createDefaultGrouping(moduleGrouper),
            group -> null, (m) -> createDistNode(m, moduleGrouper),
            getNodeComparator()
        );
        helper.createModuleNodes(Arrays.asList(modules), myRoot, getTreeModel());
    }

    private ModuleGrouper getModuleGrouper() {
        return ModuleGrouper.instanceFor(myProject, myContext.myModulesConfigurator.getModuleModel());
    }

    @NotNull
    private RakuDistNode createDistNode(Module module, ModuleGrouper moduleGrouper) {
        Perl6ModuleConfigurable configurable =
            new Perl6ModuleConfigurable(myContext.myModulesConfigurator, module, TREE_UPDATER, moduleGrouper);
        return new RakuDistNode(configurable);
    }

    @Override
    public void disposeUIResources() {
        super.disposeUIResources();
        myContext.myModulesConfigurator.disposeUIResources();
    }

    @Override
    public void dispose() {}

    @Override
    public @NotNull String getId() {
        return "Raku.Configurable.Distributions";
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Modules";
    }

    @Override
    public void init(Perl6StructureConfigurableContext context) {
        super.init(context);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (myContext.myModulesConfigurator.isModified()) {
            myContext.myModulesConfigurator.apply();
        }
    }

    @Override
    public boolean isModified() {
        return myContext.myModulesConfigurator.isModified();
    }

    @Override
    protected @Nullable AbstractAddGroup createAddAction() {
        return new AbstractAddGroup("Add") {
            @Override
            public AnAction @NotNull [] getChildren(@Nullable final AnActionEvent e) {
                AnAction addModuleAction = new AddModuleAction(false);
                addModuleAction.getTemplatePresentation().setText("New Module");
                List<AnAction> result = new ArrayList<>();
                result.add(addModuleAction);

                AnAction importModuleAction = new AddModuleAction(true);
                importModuleAction.getTemplatePresentation().setText("Import Module");
                importModuleAction.getTemplatePresentation().setIcon(AllIcons.ToolbarDecorator.Import);
                result.add(importModuleAction);

                final NullableComputable<MyNode> selectedNodeRetriever = () -> {
                    final TreePath selectionPath = myTree.getSelectionPath();
                    final Object lastPathComponent = selectionPath == null ? null : selectionPath.getLastPathComponent();
                    if (lastPathComponent instanceof MyNode) {
                        return (MyNode)lastPathComponent;
                    }
                    return null;
                };

                return result.toArray(AnAction.EMPTY_ARRAY);
            }
        };
    }

    @Nullable
    public Module getModule(final String moduleName) {
        if (moduleName == null) return null;
        return myContext != null && myContext.myModulesConfigurator != null
               ? myContext.myModulesConfigurator.getModule(moduleName)
               : myModuleManager.findModuleByName(moduleName);
    }

    public static Perl6ModuleStructureConfigurable getInstance(Project project) {
        return ServiceManager.getService(project, Perl6ModuleStructureConfigurable.class);
    }

    private class AddModuleAction extends AnAction implements DumbAware {
        private final boolean myImport;

        AddModuleAction(boolean anImport) {
            super("Module", null, AllIcons.Nodes.Module);
            myImport = anImport;
        }

        @Override
        public void actionPerformed(@NotNull final AnActionEvent e) {
            addModule(myImport);
        }
    }

    private void addModule(boolean anImport) {
        final List<Module> modules = myContext.myModulesConfigurator.addModule(myTree, anImport, "untitled");
        if (modules != null && !modules.isEmpty()) {
            //new module wizard may add yet another SDK to the project
            Perl6ProjectStructureConfigurable.getInstance(myProject).getProjectSdksModel().syncSdks();
            for (Module module : modules) {
                addModuleNode(module);
            }
        }
    }

    @Override
    protected List<? extends RemoveConfigurableHandler<?>> getRemoveHandlers() {
        return myRemoveHandlers;
    }

    private void addModuleNode(final Module module) {
        MyNode parent = myRoot;
        MyNode node = createDistNode(module, getModuleGrouper());
        TreeUtil.insertNode(node, parent, getTreeModel(), getNodeComparator());
        selectNodeInTree(node);
    }

    private class RakuDistRemoveHandler extends RemoveConfigurableHandler<Module> {
        RakuDistRemoveHandler() {
            super(Perl6ModuleConfigurable.class);
        }

        @Override
        public boolean remove(List<?> modules) {
            Perl6ModulesConfigurator modulesConfigurator = myContext.myModulesConfigurator;
            List<Module> deleted = modulesConfigurator.deleteModules(modules);
            return !deleted.isEmpty();
        }
    }
}
