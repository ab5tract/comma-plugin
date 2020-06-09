// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.project.projectWizard.components;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.ide.util.projectWizard.AbstractModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleNameLocationSettings;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import edument.perl6idea.project.structure.Perl6ProjectStructureConfigurable;
import edument.perl6idea.utils.CommaProjectWizardUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.io.File;

public class ModuleNameLocationComponent implements ModuleNameLocationSettings {
    private final WizardContext myWizardContext;
    private JTextField myModuleName;
    private TextFieldWithBrowseButton myModuleContentRoot;
    private TextFieldWithBrowseButton myModuleFileLocation;
    private JPanel myModulePanel;

    private boolean myModuleNameChangedByUser = false;
    private boolean myModuleNameDocListenerEnabled = true;

    private boolean myContentRootChangedByUser = false;
    private boolean myContentRootDocListenerEnabled = true;

    private boolean myImlLocationChangedByUser = false;
    private boolean myImlLocationDocListenerEnabled = true;

    private boolean myUpdatePathsWhenNameIsChanged;
    private boolean myUpdateNameWhenPathIsChanged;

    public ModuleNameLocationComponent(@NotNull WizardContext wizardContext) {
        myWizardContext = wizardContext;
    }

    @Nullable
    public AbstractModuleBuilder getModuleBuilder() {
        return ((AbstractModuleBuilder)myWizardContext.getProjectBuilder());
    }

    /**
     * @see ModuleWizardStep#validate()
     */
    public boolean validate() throws ConfigurationException {
        AbstractModuleBuilder builder = getModuleBuilder();
        if (builder != null && !builder.validateModuleName(getModuleName())) return false;
        if (!validateModulePaths()) return false;
        validateExistingModuleName();
        return true;
    }

    /**
     * @see ModuleWizardStep#updateDataModel()
     */
    public void updateDataModel() {
        AbstractModuleBuilder moduleBuilder = getModuleBuilder();
        if (moduleBuilder == null) return;

        final String moduleName = getModuleName();
        moduleBuilder.setName(moduleName);
        moduleBuilder.setModuleFilePath(
            FileUtil.toSystemIndependentName(myModuleFileLocation.getText()) + "/" + moduleName + ModuleFileType.DOT_DEFAULT_EXTENSION);
        moduleBuilder.setContentEntryPath(FileUtil.toSystemIndependentName(getModuleContentRoot()));
    }

    public JPanel getModulePanel() {
        return myModulePanel;
    }

    public void bindModuleSettings(final NamePathComponent namePathComponent) {
        namePathComponent.getNameComponent().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull final DocumentEvent e) {
                if (!myModuleNameChangedByUser) {
                    setModuleName(namePathComponent.getNameValue());
                }
            }
        });

        myModuleContentRoot.addBrowseFolderListener("Select Module Content Root",
                                                    "Selected directory will be marked as module content root",
                                                    myWizardContext.getProject(), BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR);

        namePathComponent.getPathComponent().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull final DocumentEvent e) {
                if (!myContentRootChangedByUser) {
                    setModuleContentRoot(namePathComponent.getPath(), true);
                }
            }
        });
        myModuleName.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull final DocumentEvent e) {
                if (!myUpdatePathsWhenNameIsChanged) {
                    return;
                }

                if (myModuleNameDocListenerEnabled) {
                    myModuleNameChangedByUser = true;
                }
                String path = getDefaultBaseDir(myWizardContext, namePathComponent);
                final String moduleName = getModuleName();
                if (path.length() > 0 && !Comparing.strEqual(moduleName, namePathComponent.getNameValue())) {
                    path += "/" + moduleName;
                }
                if (!myContentRootChangedByUser) {
                    setModuleContentRoot(path);
                }
                if (!myImlLocationChangedByUser) {
                    setImlFileLocation(path);
                }
            }
        });
        myModuleContentRoot.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull final DocumentEvent e) {
                if (myContentRootDocListenerEnabled) {
                    myContentRootChangedByUser = true;
                }
                if (!myImlLocationChangedByUser) {
                    setImlFileLocation(getModuleContentRoot());
                }
                if (!myModuleNameChangedByUser && myUpdateNameWhenPathIsChanged) {
                    final String path = FileUtil.toSystemIndependentName(getModuleContentRoot());
                    final int idx = path.lastIndexOf("/");

                    boolean oldValue = myUpdatePathsWhenNameIsChanged;
                    myUpdatePathsWhenNameIsChanged = false;
                    setModuleName(idx >= 0 ? path.substring(idx + 1) : "");
                    myUpdatePathsWhenNameIsChanged = oldValue;
                }
            }
        });

        myModuleFileLocation.addBrowseFolderListener("Select File Module Parent Directory",
                                                     "Module .iml file would be placed in selected directory",
                                                     myWizardContext.getProject(), BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR);
        myModuleFileLocation.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull final DocumentEvent e) {
                if (myImlLocationDocListenerEnabled) {
                    myImlLocationChangedByUser = true;
                }
            }
        });
        namePathComponent.getPathComponent().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull final DocumentEvent e) {
                if (!myImlLocationChangedByUser) {
                    setImlFileLocation(namePathComponent.getPath());
                }
            }
        });
        myUpdatePathsWhenNameIsChanged = true;
        if (myWizardContext.isCreatingNewProject()) {
            setModuleName(namePathComponent.getNameValue());
            setModuleContentRoot(namePathComponent.getPath());
            setImlFileLocation(namePathComponent.getPath());
        }
        else {
            final Project project = myWizardContext.getProject();
            assert project != null;
            VirtualFile baseDir = project.getBaseDir();
            if (baseDir != null) { //e.g. was deleted
                final String baseDirPath = baseDir.getPath();
                String moduleName = CommaProjectWizardUtil.findNonExistingFileName(baseDirPath, myWizardContext.getDefaultModuleName(), "");
                String contentRoot = baseDirPath + "/" + moduleName;
                if (!Comparing.strEqual(project.getName(), myWizardContext.getProjectName()) &&
                    !myWizardContext.isCreatingNewProject() &&
                    myWizardContext.getProjectName() != null) {
                    moduleName =
                        CommaProjectWizardUtil
                            .findNonExistingFileName(myWizardContext.getProjectFileDirectory(), myWizardContext.getProjectName(), "");
                    contentRoot = myWizardContext.getProjectFileDirectory();
                    myUpdatePathsWhenNameIsChanged = !myWizardContext.isProjectFileDirectorySetExplicitly();
                }
                setModuleName(moduleName);
                setModuleContentRoot(contentRoot);
                setImlFileLocation(contentRoot);
                myModuleName.select(0, moduleName.length());
            }
        }
    }

    private void validateExistingModuleName() throws ConfigurationException {
        Project project = myWizardContext.getProject();
        if (project == null) return;

        final String moduleName = getModuleName();
        final Module module;
        final Perl6ProjectStructureConfigurable fromConfigurable = Perl6ProjectStructureConfigurable.getInstance(project);
        if (fromConfigurable != null) {
            module = fromConfigurable.getModulesConfig().getModule(moduleName);
        }
        else {
            module = ModuleManager.getInstance(project).findModuleByName(moduleName);
        }
        if (module != null) {
            throw new ConfigurationException("Module \'" + moduleName + "\' already exist in project. Please, specify another name.");
        }
    }

    private boolean validateModulePaths() throws ConfigurationException {
        final String moduleName = getModuleName();
        final String moduleFileDirectory = myModuleFileLocation.getText();
        if (moduleFileDirectory.isEmpty()) {
            throw new ConfigurationException("Enter module file location");
        }
        if (moduleName.isEmpty()) {
            throw new ConfigurationException("Enter a module name");
        }

        if (!CommaProjectWizardUtil.createDirectoryIfNotExists("The module file directory\n", moduleFileDirectory,
                                                               myImlLocationChangedByUser)) {
            return false;
        }
        if (!CommaProjectWizardUtil
            .createDirectoryIfNotExists("The module content root.\n", myModuleContentRoot.getText(),
                                        myContentRootChangedByUser)) {
            return false;
        }

        File moduleFile = new File(moduleFileDirectory, moduleName + ModuleFileType.DOT_DEFAULT_EXTENSION);
        if (moduleFile.exists()) {
            int answer = Messages.showYesNoDialog(String.format("The %s file \n%s\nalready exists.\nWould you like to overwrite it?",
                                                                IdeBundle.message("project.new.wizard.module.identification"), moduleFile.getAbsolutePath()),
                                                  IdeBundle.message("title.file.already.exists"), Messages.getQuestionIcon());
            if (answer != Messages.YES) {
                return false;
            }
        }
        return true;
    }

    @Override
    @NotNull
    public String getModuleContentRoot() {
        return myModuleContentRoot.getText();
    }

    private static String getDefaultBaseDir(WizardContext wizardContext, NamePathComponent namePathComponent) {
        if (wizardContext.isCreatingNewProject()) {
            return namePathComponent.getPath();
        }
        else {
            final Project project = wizardContext.getProject();
            assert project != null;
            final VirtualFile baseDir = project.getBaseDir();
            if (baseDir != null) {
                return baseDir.getPath();
            }
            return "";
        }
    }

    private void setImlFileLocation(final String path) {
        myImlLocationDocListenerEnabled = false;
        myModuleFileLocation.setText(FileUtil.toSystemDependentName(path));
        myImlLocationDocListenerEnabled = true;
    }

    @Override
    public void setModuleContentRoot(@NotNull final String path) {
        setModuleContentRoot(path, false);
    }

    private void setModuleContentRoot(@NotNull String path, boolean updateName) {
        myUpdateNameWhenPathIsChanged = updateName;
        myContentRootDocListenerEnabled = false;
        myModuleContentRoot.setText(FileUtil.toSystemDependentName(path));
        myContentRootDocListenerEnabled = true;
        myUpdateNameWhenPathIsChanged = true;
    }

    @Override
    public void setModuleName(@NotNull String moduleName) {
        myModuleNameDocListenerEnabled = false;
        myModuleName.setText(moduleName);
        myModuleNameDocListenerEnabled = true;
    }

    public JTextField getModuleNameField() {
        return myModuleName;
    }

    @Override
    @NotNull
    public String getModuleName() {
        return myModuleName.getText().trim();
    }
}
