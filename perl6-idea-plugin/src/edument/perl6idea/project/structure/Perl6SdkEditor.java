// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.project.structure;

import com.intellij.ide.plugins.newui.TwoLineProgressIndicator;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.projectRoots.ui.PathEditor;
import com.intellij.openapi.projectRoots.ui.SdkPathEditor;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.impl.status.InlineProgressIndicator;
import com.intellij.ui.TabbedPaneWrapper;
import com.intellij.ui.navigation.History;
import com.intellij.ui.navigation.Place;
import com.intellij.util.Consumer;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class Perl6SdkEditor implements Configurable, Place.Navigator {
    private static final Logger LOG = Logger.getInstance(Perl6SdkEditor.class);
    private static final String SDK_TAB = "sdkTab";
    public static final String COMPILER_HOME_LABEL = "Raku Compiler Home:";

    @NotNull
    private final Sdk mySdk;
    private final Map<OrderRootType, SdkPathEditor> myPathEditors = new HashMap<>();

    private TextFieldWithBrowseButton myHomeComponent;
    private final Map<SdkType, List<AdditionalDataConfigurable>> myAdditionalDataConfigurables = new HashMap<>();
    private final Map<AdditionalDataConfigurable, JComponent> myAdditionalDataComponents = new HashMap<>();
    private JPanel myAdditionalDataPanel;
    private JPanel myDownloadingPanel;
    private InlineProgressIndicator myDownloadProgressIndicator;

    // GUI components
    private JPanel myMainPanel;
    private TabbedPaneWrapper myTabbedPane;
    private final Project myProject;
    private final SdkModel mySdkModel;
    private JLabel myHomeFieldLabel;
    private String myVersionString;

    private String myInitialName;
    private String myInitialPath;
    private final boolean myIsDownloading = false;
    private final History myHistory;

    private final Disposable myDisposable = Disposer.newDisposable();

    private boolean myIsDisposed = false;
    private final Consumer<Boolean> myResetCallback = __ -> {
        if (!myIsDisposed) reset();
    };

    public Perl6SdkEditor(@NotNull Project project,
                          @NotNull SdkModel sdkModel,
                          @NotNull History history,
                          @NotNull ProjectJdkImpl sdk) {
        myProject = project;
        mySdkModel = sdkModel;
        myHistory = history;
        mySdk = sdk;
        myInitialName = mySdk.getName();
        myInitialPath = mySdk.getHomePath();
        createMainPanel();
        reset();
    }

    @Override
    public String getDisplayName() {
        return "SDK Editor";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return myMainPanel;
    }

    private void createMainPanel() {
        myMainPanel = new JPanel(new GridBagLayout());

        myTabbedPane = new TabbedPaneWrapper(myDisposable);

        myTabbedPane.addChangeListener(e -> myHistory.pushQueryPlace());

        myHomeComponent = createHomeComponent();
        myHomeComponent.getTextField().setEditable(false);
        myHomeFieldLabel = new JLabel("Raku compiler home:");
        myMainPanel.add(myHomeFieldLabel, new GridBagConstraints(
            0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, JBUI.insets(2, 10, 2, 2), 0,
            0));
        myMainPanel.add(myHomeComponent, new GridBagConstraints(
            1, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            JBUI.insets(2, 2, 2, 10), 0, 0));

        myAdditionalDataPanel = new JPanel(new BorderLayout());
        myMainPanel.add(myAdditionalDataPanel, new GridBagConstraints(
            0, GridBagConstraints.RELATIVE, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, JBUI.insets(2, 10, 0, 10),
            0, 0));

        myMainPanel.add(myTabbedPane.getComponent(), new GridBagConstraints(
            0, GridBagConstraints.RELATIVE, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, JBUI.insetsTop(2), 0, 0));

        myDownloadingPanel = new JPanel(new BorderLayout());
        myDownloadProgressIndicator = new TwoLineProgressIndicator(true);
        myDownloadProgressIndicator.setIndeterminate(true);
        myDownloadingPanel.add(myDownloadProgressIndicator.getComponent(), BorderLayout.NORTH);
        myDownloadProgressIndicator.getComponent().setMaximumSize(JBUI.size(300, 200));

        myMainPanel.add(myDownloadingPanel, new GridBagConstraints(
            0, GridBagConstraints.RELATIVE, 2, 1, 0, 1.0, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, JBUI.insets(8, 10, 0, 10), 0,
            0));
    }

    protected TextFieldWithBrowseButton createHomeComponent() {
        return new TextFieldWithBrowseButton(e -> doSelectHomePath());
    }

    private void doSelectHomePath() {
        final SdkType sdkType = (SdkType)mySdk.getSdkType();
        SdkConfigurationUtil.selectSdkHome(sdkType, path -> doSetHomePath(path, sdkType));
    }

    private void doSetHomePath(final String homePath, final SdkType sdkType) {
        if (homePath == null) {
            return;
        }
        setHomePathValue(homePath.replace('/', File.separatorChar));

        final String newSdkName = suggestSdkName(homePath);
        ((ProjectJdkImpl)mySdk).setName(newSdkName);

        try {
            final Sdk dummySdk = (Sdk)mySdk.clone();
            SdkModificator sdkModificator = dummySdk.getSdkModificator();
            sdkModificator.setHomePath(homePath);
            sdkModificator.removeAllRoots();
            sdkModificator.commitChanges();

            sdkType.setupSdkPaths(dummySdk, mySdkModel);

            clearAllPaths();
            myVersionString = dummySdk.getVersionString();
            if (myVersionString == null) {
                Messages.showMessageDialog("Foo", "Bar", Messages.getErrorIcon());
            }
            sdkModificator = dummySdk.getSdkModificator();
            for (OrderRootType type : myPathEditors.keySet()) {
                SdkPathEditor pathEditor = myPathEditors.get(type);
                pathEditor.setAddBaseDir(dummySdk.getHomeDirectory());
                pathEditor.addPaths(sdkModificator.getRoots(type));
            }
            mySdkModel.getMulticaster().sdkHomeSelected(dummySdk, homePath);
        }
        catch (CloneNotSupportedException e) {
            LOG.error(e); // should not happen in normal program
        }
    }

    private String suggestSdkName(final String homePath) {
        final String currentName = mySdk.getName();
        final String suggestedName = ((SdkType)mySdk.getSdkType()).suggestSdkName(currentName, homePath);
        if (Comparing.equal(currentName, suggestedName)) return currentName;
        String newSdkName = suggestedName;
        final Set<String> allNames = new HashSet<>();
        Sdk[] sdks = mySdkModel.getSdks();
        for (Sdk sdk : sdks) {
            allNames.add(sdk.getName());
        }
        int i = 0;
        while (allNames.contains(newSdkName)) {
            newSdkName = suggestedName + " (" + ++i + ")";
        }
        return newSdkName;
    }

    private void clearAllPaths() {
        for (PathEditor editor : myPathEditors.values()) {
            editor.clearList();
        }
    }

    private void setHomePathValue(String absolutePath) {
        myHomeComponent.setText(absolutePath);
        final Color fg;
        if (absolutePath != null && !absolutePath.isEmpty() && mySdk.getSdkType().isLocalSdk(mySdk)) {
            final File homeDir = new File(absolutePath);
            boolean homeMustBeDirectory = ((SdkType)mySdk.getSdkType()).getHomeChooserDescriptor().isChooseFolders();
            fg = homeDir.exists() && homeDir.isDirectory() == homeMustBeDirectory
                 ? UIUtil.getFieldForegroundColor()
                 : PathEditor.INVALID_COLOR;
        }
        else {
            fg = UIUtil.getFieldForegroundColor();
        }
        myHomeComponent.getTextField().setForeground(fg);
    }


    @Override
    public boolean isModified() {
        boolean isModified = !Comparing.equal(mySdk.getName(), myInitialName);
        if (myIsDownloading) return isModified;

        isModified =
            isModified ||
            !Comparing.equal(FileUtil.toSystemIndependentName(getHomeValue()), FileUtil.toSystemIndependentName(myInitialPath));
        for (PathEditor pathEditor : myPathEditors.values()) {
            isModified = isModified || pathEditor.isModified();
        }
        return isModified;
    }

    @Override
    public void reset() {
        // XXX Provide a way to download Sdk here?
        //myIsDownloading = SdkDownloadTracker.getInstance()
        //    .tryRegisterDownloadingListener(mySdk, myDisposable, myDownloadProgressIndicator, myResetCallback);
        // Always false for now
        if (!myIsDownloading) {
            final SdkModificator sdkModificator = mySdk.getSdkModificator();
            for (OrderRootType type : myPathEditors.keySet()) {
                myPathEditors.get(type).reset(sdkModificator);
            }
            sdkModificator.commitChanges();
        }

        setHomePathValue(FileUtil.toSystemDependentName(ObjectUtils.notNull(mySdk.getHomePath(), "")));
        myVersionString = null;
        myHomeFieldLabel.setText(COMPILER_HOME_LABEL);

        myTabbedPane.getComponent().setVisible(!myIsDownloading);
        myAdditionalDataPanel.setVisible(!myIsDownloading);
        myDownloadingPanel.setVisible(myIsDownloading);
        myHomeComponent.setEnabled(!myIsDownloading);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (myIsDownloading) return;

        if (!Comparing.equal(myInitialName, mySdk.getName())) {
            if (mySdk.getName().isEmpty()) {
                throw new ConfigurationException("Please specify Raku compiler installation name");
            }
        }
        myInitialName = mySdk.getName();
        myInitialPath = mySdk.getHomePath();
        SdkModificator sdkModificator = mySdk.getSdkModificator();
        sdkModificator.setHomePath(FileUtil.toSystemIndependentName(getHomeValue()));
        for (SdkPathEditor pathEditor : myPathEditors.values()) {
            pathEditor.apply(sdkModificator);
        }
        ApplicationManager.getApplication().runWriteAction(sdkModificator::commitChanges);
    }

    @Override
    public void disposeUIResources() {
        myIsDisposed = true;
        for (final SdkType sdkType : myAdditionalDataConfigurables.keySet()) {
            for (final AdditionalDataConfigurable configurable : myAdditionalDataConfigurables.get(sdkType)) {
                configurable.disposeUIResources();
            }
        }
        myAdditionalDataConfigurables.clear();
        myAdditionalDataComponents.clear();

        Disposer.dispose(myDisposable);
    }

    private String getHomeValue() {
        return myHomeComponent.getText().trim();
    }
}
