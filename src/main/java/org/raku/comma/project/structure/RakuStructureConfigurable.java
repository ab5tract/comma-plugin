package org.raku.comma.project.structure;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MasterDetailsComponent;
import com.intellij.openapi.ui.MasterDetailsStateService;
import com.intellij.openapi.ui.NamedConfigurable;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.navigation.Place;
import com.intellij.util.IconUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.*;
import java.util.function.Predicate;

public abstract class RakuStructureConfigurable extends MasterDetailsComponent
                                                implements SearchableConfigurable,
                                                           Disposable,
                                                           Place.Navigator
{
    protected final Project myProject;
    protected boolean myWasTreeInitialized;
    protected boolean myUiDisposed;
    protected RakuStructureConfigurableContext myContext;

    protected RakuStructureConfigurable(@NotNull Project project) {
        myProject = project;
    }

    @Override
    protected void initTree() {
        if (myWasTreeInitialized) return;
        myWasTreeInitialized = true;
        super.initTree();
        TreeSpeedSearch.installOn(myTree, true, treePath -> getTextForSpeedSearch((MyNode) treePath.getLastPathComponent()));
        // TODO is it even needed to port this?
        //myTree.setCellRenderer(new Perl6StructureElementRenderer());
    }

    private static String getTextForSpeedSearch(MyNode component) {
        return component.getDisplayName();
    }

    @Override
    protected @Nullable MasterDetailsStateService getStateService() {
        return MasterDetailsStateService.getInstance(myProject);
    }

    @Override
    public void disposeUIResources() {
        if (myUiDisposed) return;
        super.disposeUIResources();
        myUiDisposed = true;
        myAutoScrollHandler.cancelAllRequests();
        Disposer.dispose(this);
    }

    @Override
    public void reset() {
        myUiDisposed = false;

        if (!myWasTreeInitialized) {
            initTree();
            myTree.setShowsRootHandles(false);
            loadTreeNodes();
        }
        else {
            reloadTreeNodes();
        }

        super.reset();
    }

    private void loadTreeNodes() {
        loadTree();
    }

    public void init(RakuStructureConfigurableContext context) {
        myContext = context;
        myTree.revalidate();
        myTree.repaint();
    }

    protected abstract void loadTree();

    protected final void reloadTreeNodes() {
        super.disposeUIResources();
        myTree.setShowsRootHandles(false);
        loadTreeNodes();
    }

    @Override
    @NotNull
    protected ArrayList<AnAction> createActions(final boolean fromPopup) {
        final ArrayList<AnAction> result = new ArrayList<>();
        AbstractAddGroup addAction = createAddAction();
        if (addAction != null) {
            result.add(addAction);
        }
        result.add(new MyRemoveAction());
        result.add(Separator.getInstance());
        return result;
    }

    @Nullable
    protected abstract AbstractAddGroup createAddAction();

    protected class MyRemoveAction extends MyDeleteAction {
        public MyRemoveAction() {
            //noinspection Convert2Lambda
            super(new Predicate<>() {
                @Override
                public boolean test(final Object[] objects) {
                    List<MyNode> nodes = new ArrayList<>();
                    for (Object object : objects) {
                        if (!(object instanceof MyNode)) return false;
                        nodes.add((MyNode)object);
                    }
                    MultiMap<RemoveConfigurableHandler<?>, MyNode> map = groupNodes(nodes);
                    for (Map.Entry<RemoveConfigurableHandler<?>, Collection<MyNode>> entry : map.entrySet()) {
                        if (!entry.getKey().canBeRemoved(getEditableObjects(entry.getValue()))) {
                            return false;
                        }
                    }
                    return true;
                }
            });
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            final TreePath[] paths = myTree.getSelectionPaths();
            if (paths == null) return;

            List<MyNode> removedNodes = removeFromModel(paths);
            removeNodes(removedNodes);
        }

        private List<MyNode> removeFromModel(final TreePath[] paths) {
            List<MyNode> nodes = ContainerUtil.mapNotNull(paths, path -> {
                Object node = path.getLastPathComponent();
                return node instanceof MyNode ? (MyNode)node : null;
            });
            MultiMap<RemoveConfigurableHandler<?>, MyNode> grouped = groupNodes(nodes);

            List<MyNode> removedNodes = new ArrayList<>();
            for (Map.Entry<RemoveConfigurableHandler<?>, Collection<MyNode>> entry : grouped.entrySet()) {
                boolean removed = entry.getKey().remove(getEditableObjects(entry.getValue()));
                if (removed) {
                    removedNodes.addAll(entry.getValue());
                }
            }
            return removedNodes;
        }
    }

    private static List<?> getEditableObjects(Collection<MyNode> value) {
        List<Object> objects = new ArrayList<>();
        for (MyNode node : value) {
            objects.add(node.getConfigurable().getEditableObject());
        }
        return objects;
    }

    @NotNull
    private MultiMap<RemoveConfigurableHandler<?>, MyNode> groupNodes(List<? extends MyNode> nodes) {
        List<? extends RemoveConfigurableHandler<?>> handlers = getRemoveHandlers();
        MultiMap<RemoveConfigurableHandler<?>, MyNode> grouped = MultiMap.createConcurrent();
        for (MyNode node : nodes) {
            final NamedConfigurable<?> configurable = node.getConfigurable();
            if (configurable == null) continue;
            RemoveConfigurableHandler<?> handler = findHandler(handlers, configurable.getClass());
            if (handler == null) continue;

            grouped.putValue(handler, node);
        }
        return grouped;
    }

    protected List<? extends RemoveConfigurableHandler<?>> getRemoveHandlers() {
        return Collections.emptyList();
    }

    private static RemoveConfigurableHandler<?> findHandler(List<? extends RemoveConfigurableHandler<?>> handlers,
                                                            Class<? extends NamedConfigurable> configurableClass) {
        for (RemoveConfigurableHandler<?> handler : handlers) {
            if (handler.getConfigurableClass().isAssignableFrom(configurableClass)) {
                return handler;
            }
        }
        return null;
    }

    protected abstract static class AbstractAddGroup extends ActionGroup implements ActionGroupWithPreselection {
        protected AbstractAddGroup(String text, Icon icon) {
            super(text, true);

            final Presentation presentation = getTemplatePresentation();
            presentation.setIcon(icon);

            KeymapManager keymapManager = KeymapManager.getInstance();
            if (keymapManager != null) {
                final Keymap active = keymapManager.getActiveKeymap();
                final Shortcut[] shortcuts = active.getShortcuts("NewElement");
                setShortcutSet(new CustomShortcutSet(shortcuts));
            }
        }

        public AbstractAddGroup(@Nls String text) {
            this(text, IconUtil.getAddIcon());
        }

        @Override
        public ActionGroup getActionGroup() {
            return this;
        }
    }
}
