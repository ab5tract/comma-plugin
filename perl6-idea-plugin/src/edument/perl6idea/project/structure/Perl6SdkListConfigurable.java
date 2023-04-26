// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.project.structure;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.SdkPopupFactory;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.MasterDetailsComponent;
import com.intellij.openapi.ui.NamedConfigurable;
import com.intellij.util.IconUtil;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;
import java.util.*;

public class Perl6SdkListConfigurable extends Perl6StructureConfigurable {
    private final SdkModel.Listener myListener = new SdkModel.Listener() {
        @Override
        public void sdkAdded(@NotNull Sdk sdk) {
            addSdkNode(sdk, true);
        }

        @Override
        public void sdkChanged(@NotNull Sdk sdk, String previousName) {
            updateName();
        }

        @Override
        public void sdkHomeSelected(@NotNull Sdk sdk, @NotNull String newSdkHome) {
            updateName();
        }

        private void updateName() {
            final TreePath path = myTree.getSelectionPath();
            if (path != null) {
                final NamedConfigurable<?> configurable = ((MyNode)path.getLastPathComponent()).getConfigurable();
                if (configurable instanceof Perl6SdkConfigurable) {
                    configurable.updateName();
                }
            }
        }
    };
    @NotNull
    private final ProjectSdksModel myJdksTreeModel;

    protected Perl6SdkListConfigurable(@NotNull Project project) {
        super(project);
        myJdksTreeModel = Perl6ProjectStructureConfigurable.getInstance(project).getProjectSdksModel();
    }

    public static Perl6SdkListConfigurable getInstance(Project project) {
        return project.getService(Perl6SdkListConfigurable.class);
    }

    @Override
    protected void loadTree() {
        final Map<Sdk, Sdk> sdks = myJdksTreeModel.getProjectSdks();
        for (Sdk sdk : sdks.keySet()) {
            Perl6SdkConfigurable configurable =
                new Perl6SdkConfigurable((ProjectJdkImpl)sdks.get(sdk), myJdksTreeModel, TREE_UPDATER, myHistory,
                                         myProject);
            addNode(new MyNode(configurable), myRoot);
        }
    }

    public boolean addSdkNode(final Sdk sdk, final boolean selectInTree) {
        if (!myUiDisposed) {
            addNode(new MyNode(new Perl6SdkConfigurable((ProjectJdkImpl)sdk, myJdksTreeModel, TREE_UPDATER, myHistory, myProject)), myRoot);
            if (selectInTree) {
                selectNodeInTree(MasterDetailsComponent.findNodeByObject(myRoot, sdk));
            }
            return true;
        }
        return false;
    }

    @Override
    protected @NotNull ArrayList<AnAction> createActions(boolean fromPopup) {
        ArrayList<AnAction> defaultActions = super.createActions(fromPopup);
        AnAction addNewAction = new AddSdkAction();
        defaultActions.add(0, addNewAction);
        return defaultActions;
    }

    @Override
    protected @Nullable AbstractAddGroup createAddAction() {
        return null;
    }

    @Override
    protected List<? extends RemoveConfigurableHandler<?>> getRemoveHandlers() {
        return Collections.singletonList(new SdkRemoveHandler());
    }

    @Override
    public void dispose() {
        myJdksTreeModel.removeListener(myListener);
        myJdksTreeModel.disposeUIResources();
    }

    @Override
    public boolean isModified() {
        return super.isModified() || myJdksTreeModel.isModified();
    }

    @Override
    public void reset() {
        super.reset();
        myJdksTreeModel.addListener(myListener);
        myTree.setRootVisible(false);
    }

    @Override
    public void apply() throws ConfigurationException {
        boolean modifiedJdks = false;
        for (int i = 0; i < myRoot.getChildCount(); i++) {
            final NamedConfigurable<?> configurable = ((MyNode)myRoot.getChildAt(i)).getConfigurable();
            if (configurable.isModified()) {
                configurable.apply();
                modifiedJdks = true;
            }
        }

        if (myJdksTreeModel.isModified() || modifiedJdks) myJdksTreeModel.apply(this);
        myJdksTreeModel.setProjectSdk(ProjectRootManager.getInstance(myProject).getProjectSdk());
    }

    @Override
    public @NotNull String getId() {
        return "raku.sdk.list";
    }

    @Override
    @Nls(capitalization = Nls.Capitalization.Title)
    public String getDisplayName() {
        return "Raku Compilers";
    }

    private class SdkRemoveHandler extends RemoveConfigurableHandler<Sdk> {
        SdkRemoveHandler() {
            super(Perl6SdkConfigurable.class);
        }

        @Override
        public boolean remove(@NotNull List<?> sdks) {
            for (Object sdk : sdks) {
                if (sdk instanceof Sdk)
                    myJdksTreeModel.removeSdk((Sdk)sdk);
            }
            return true;
        }
    }

    private class AddSdkAction extends AnAction implements DumbAware {
        AddSdkAction() {
            super("Add New SDK", null, IconUtil.getAddIcon());

            AbstractAddGroup replacedAction = new AbstractAddGroup("text") {
                @Override
                public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
                    return AnAction.EMPTY_ARRAY;
                }
            };
            this.setShortcutSet(replacedAction.getShortcutSet());
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            e.getPresentation().setEnabledAndVisible(true);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            SdkPopupFactory
                .newBuilder()
                .withProject(myProject)
                .withProjectSdksModel(myJdksTreeModel)
                .withSdkTypeFilter((sdkType) -> sdkType instanceof Perl6SdkType)
                .withSdkFilter(sdk -> false)
                .buildPopup()
                .showPopup(e);
        }
    }
}
