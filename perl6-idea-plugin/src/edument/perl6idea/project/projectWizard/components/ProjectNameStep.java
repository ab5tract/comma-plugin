// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.project.projectWizard.components;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.IdeCoreBundle;
import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.highlighter.ProjectFileType;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.JBInsets;
import edument.perl6idea.utils.CommaProjectWizardUtil;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

import static com.intellij.openapi.components.StorageScheme.DIRECTORY_BASED;

/**
 * @author Eugene Zhuravlev
 */
public class ProjectNameStep extends ModuleWizardStep {
    private final NamePathComponent myNamePathComponent;
    private final JPanel myPanel;
    private final WizardContext myWizardContext;

    public ProjectNameStep(WizardContext wizardContext) {
        myWizardContext = wizardContext;
        myNamePathComponent = new NamePathComponent(myWizardContext.isCreatingNewProject() ? "Project name:" : "Module name:",
                                                    String.format("%s file location:", StringUtil.capitalize(myWizardContext.getPresentationName())),
                                                    String.format("Select %s file directory", myWizardContext.getPresentationName()),
                                                    String.format("%s file will be stored in this directory", myWizardContext.getPresentationName()));
        myPanel = new JPanel(new GridBagLayout());
        myPanel.setBorder(BorderFactory.createEtchedBorder());

        String appName = ApplicationNamesInfo.getInstance().getFullProductName();
        myPanel.add(new JLabel(String.format("Please enter a name to create a new %s %s.", appName, wizardContext.getPresentationName())),
                    new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                                           GridBagConstraints.HORIZONTAL,
                                           JBInsets.create(8, 10), 0, 0));

        myPanel.add(myNamePathComponent,
                    new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST,
                                           GridBagConstraints.HORIZONTAL,
                                           JBInsets.create(8, 10), 0, 0));
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return myNamePathComponent.getNameComponent();
    }

    @Override
    public String getHelpId() {
        return "reference.dialogs.new.project.import.name";
    }

    @Override
    public JComponent getComponent() {
        return myPanel;
    }

    @Override
    public void updateStep() {
        super.updateStep();
        myNamePathComponent.setPath(FileUtil.toSystemDependentName(myWizardContext.getProjectFileDirectory()));
        String name = myWizardContext.getProjectName();
        if (name == null) {
            List<String> components = StringUtil.split(FileUtil.toSystemIndependentName(myWizardContext.getProjectFileDirectory()), "/");
            if (!components.isEmpty()) {
                name = components.get(components.size() - 1);
            }
        }
        myNamePathComponent.setNameValue(name);
        if (name != null) {
            myNamePathComponent.getNameComponent().setSelectionStart(0);
            myNamePathComponent.getNameComponent().setSelectionEnd(name.length());
        }
    }

    @Override
    public void updateDataModel() {
        myWizardContext.setProjectName(getProjectName());
        myWizardContext.setProjectFileDirectory(getProjectFileDirectory());
    }

    @Override
    public Icon getIcon() {
        return myWizardContext.getStepIcon();
    }

    @Override
    public boolean validate() throws ConfigurationException {
        String name = myNamePathComponent.getNameValue();
        if (name.length() == 0) {
            ApplicationNamesInfo info = ApplicationNamesInfo.getInstance();
            throw new ConfigurationException(
                String.format("Enter a file name to create a new %s %s", info.getFullProductName(), myWizardContext.getPresentationName()));
        }

        final String projectFileDirectory = getProjectFileDirectory();
        if (projectFileDirectory.length() == 0) {
            throw new ConfigurationException(
                String.format("Enter %s file location", myWizardContext.getPresentationName()));
        }

        final boolean shouldPromptCreation = myNamePathComponent.isPathChangedByUser();
        String prefix = String.format("The %s directory", myWizardContext.getPresentationName());
        if (!CommaProjectWizardUtil.createDirectoryIfNotExists(prefix, projectFileDirectory, shouldPromptCreation)) {
            return false;
        }

        boolean shouldContinue = true;

        final String path = myWizardContext.isCreatingNewProject() && myWizardContext.getProjectStorageFormat() == DIRECTORY_BASED
                            ? getProjectFileDirectory() + "/" + Project.DIRECTORY_STORE_FOLDER : getProjectFilePath();
        final File projectFile = new File(path);
        if (projectFile.exists()) {
            final String title = myWizardContext.isCreatingNewProject()
                                 ? IdeCoreBundle.message("title.new.project")
                                 : IdeCoreBundle.message("title.add.module");
            final String message = myWizardContext.isCreatingNewProject() && myWizardContext.getProjectStorageFormat() == DIRECTORY_BASED
                                   ? String.format("%s folder already exists in %s.\nIts content may be overwritten.\nContinue?",
                                                   Project.DIRECTORY_STORE_FOLDER, projectFile.getParentFile().getAbsolutePath())
                                   : String.format("The %s file \n%s\nalready exists.\nWould you like to overwrite it?",
                                                   myWizardContext.getPresentationName(), projectFile.getAbsolutePath());
            int answer = Messages.showYesNoDialog(message, title, Messages.getQuestionIcon());
            shouldContinue = answer == Messages.YES;
        }

        return shouldContinue;
    }

    @NonNls
    public String getProjectFilePath() {
        return getProjectFileDirectory() + "/" + myNamePathComponent.getNameValue()/*myTfProjectName.getText().trim()*/ +
               (myWizardContext.getProject() == null ? ProjectFileType.DOT_DEFAULT_EXTENSION : ModuleFileType.DOT_DEFAULT_EXTENSION);
    }

    public String getProjectFileDirectory() {
        return FileUtil.toSystemIndependentName(myNamePathComponent.getPath());
    }

    public String getProjectName() {
        return myNamePathComponent.getNameValue();
    }

    @Override
    public String getName() {
        return "Name";
    }

    @Override
    public boolean isStepVisible() {
        final ProjectBuilder builder = myWizardContext.getProjectBuilder();
        if (builder != null && builder.isUpdate()) return false;
        return super.isStepVisible();
    }
}
