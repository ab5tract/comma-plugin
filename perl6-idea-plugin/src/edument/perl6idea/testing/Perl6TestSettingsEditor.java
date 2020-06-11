package edument.perl6idea.testing;

import com.intellij.ProjectTopics;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Function;
import edument.perl6idea.module.Perl6ModuleType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;

public class Perl6TestSettingsEditor extends SettingsEditor<Perl6TestRunConfiguration> {
    protected Project myProject;
    protected JComboBox<RakUTestKind> myKindField;
    protected JComboBox<String> myModuleNameField;
    protected TextFieldWithBrowseButton myDirectoryPathField;
    protected JTextField myFilePatternField;
    protected TextFieldWithBrowseButton myFilePathField;
    protected JTextField myDegreeField;
    protected EnvironmentVariablesComponent myEnvVariablesField;
    private LabeledComponent<JComboBox<RakUTestKind>> myTestKind;
    private LabeledComponent<JComboBox<String>> myModuleName;
    private LabeledComponent<TextFieldWithBrowseButton> myDirectoryPath;
    private LabeledComponent<JTextField> myFilePattern;
    private LabeledComponent<TextFieldWithBrowseButton> myFilePath;

    public Perl6TestSettingsEditor(Project project) {
        myProject = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull Perl6TestRunConfiguration configuration) {
        // Specific options
        myKindField.setSelectedItem(configuration.getTestKind());
        String moduleNameFromConfig = configuration.getModuleName();
        myModuleNameField.setSelectedItem(moduleNameFromConfig);
        myDirectoryPathField.setText(configuration.getDirectoryPath());
        myFilePatternField.setText(configuration.getFilePattern());
        myFilePathField.setText(configuration.getFilePath());
        // Generic options
        myDegreeField.setText(String.valueOf(configuration.getParallelismDegree()));
        myEnvVariablesField.setEnvs(configuration.getEnvs());
        myEnvVariablesField.setPassParentEnvs(configuration.isPassParentEnvs());
    }

    @Override
    protected void applyEditorTo(@NotNull Perl6TestRunConfiguration configuration) throws ConfigurationException {
        // Specific options
        configuration.setTestKind((RakUTestKind)myKindField.getSelectedItem());
        switch ((RakUTestKind)myKindField.getSelectedItem()) {
            case ALL:
                break;
            case MODULE: {
                String module = (String)myModuleNameField.getSelectedItem();
                if (module == null)
                    throw new ConfigurationException("Module must be specified");
                if (ModuleManager.getInstance(myProject).findModuleByName(module) == null)
                    throw new ConfigurationException("Module is incorrect");
                configuration.setModuleName(module);
                break;
            }
            case DIRECTORY: {
                String dirPath = myDirectoryPathField.getText();
                VirtualFile dir = LocalFileSystem.getInstance().findFileByPath(dirPath);
                if (dirPath.isEmpty() || dir == null || !dir.exists() || !dir.isDirectory()) {
                    throw new ConfigurationException("A directory must be specified");
                } else {
                    configuration.setDirectoryPath(dirPath);
                }
                break;
            }
            case PATTERN: {
                if (myFilePatternField.getText().isEmpty()) {
                    throw new ConfigurationException("A pattern must be specified");
                } else {
                    configuration.setFilePattern(myFilePatternField.getText());
                }
                break;
            }
            case FILE: {
                String filepath = myFilePathField.getText();
                VirtualFile file = LocalFileSystem.getInstance().findFileByPath(filepath);
                if (filepath.isEmpty() || file == null || !file.exists()) {
                    throw new ConfigurationException("A file must be specified");
                } else {
                    configuration.setFilePath(filepath);
                }
                break;
            }
        }
        // Generic options
        try {
            int degree = Integer.parseInt(myDegreeField.getText());
            configuration.setParallelismDegree(degree);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Number of tests must be a positive integer");
        }
        configuration.setEnvs(myEnvVariablesField.getEnvs());
        configuration.setPassParentEnvs(myEnvVariablesField.isPassParentEnvs());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JPanel panel = new JPanel();
        panel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 5, true, false));

        // Create controls
        myKindField = new ComboBox<>(RakUTestKind.values());
        myKindField.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                reloadSpecificItems();
            }
        });

        myModuleNameField = new ComboBox<>();
        myModuleNameField.setModel(new RakuModuleModel(myProject));

        myDirectoryPathField = new TextFieldWithBrowseButton();
        myDirectoryPathField.addBrowseFolderListener(
            "Select Directory", null, myProject,
            new FileChooserDescriptor(
                false, true, false, false, false, false
            ));

        myFilePatternField = new JTextField();

        myFilePathField = new TextFieldWithBrowseButton();
        myFilePathField.addBrowseFolderListener(
            "Select Test File", null, myProject,
            new FileChooserDescriptor(
                true, false, false, false, false, false
            ) {
                @Override
                public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                    return file.isDirectory() || file.getExtension() == null
                           || Arrays.asList("t", "t6", "rakutest").contains(file.getExtension());
                }
            }
        );

        myDegreeField = new JTextField(String.valueOf(1)) {
            @Override
            protected Document createDefaultModel() {
                return new PlainDocument() {
                    @Override
                    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                        if (str == null) return;
                        String oldString = getText(0, getLength());
                        String newString = oldString.substring(0, offs) + str + oldString.substring(offs);
                        try {
                            // Check if we can parse string as int
                            int degree = Integer.parseInt(newString);
                            if (degree > 0)
                                super.insertString(offs, str, a);
                        }
                        catch (NumberFormatException ignored) {
                        }
                    }
                };
            }
        };
        LabeledComponent<JTextField> degree = LabeledComponent.create(myDegreeField, "Tests run in parallel", BorderLayout.WEST);
        myEnvVariablesField = new EnvironmentVariablesComponent();

        setupSpecificComponents(panel);
        panel.add(degree);
        panel.add(myEnvVariablesField);

        return panel;
    }

    private void setupSpecificComponents(JPanel panel) {
        myTestKind = LabeledComponent.create(myKindField, "Test kind", BorderLayout.WEST);
        panel.add(myTestKind);
        myModuleName = LabeledComponent.create(myModuleNameField, "Module", BorderLayout.WEST);
        panel.add(myModuleName);
        myDirectoryPath = LabeledComponent.create(myDirectoryPathField, "Directory", BorderLayout.WEST);
        panel.add(myDirectoryPath);
        myFilePattern = LabeledComponent.create(myFilePatternField, "Glob pattern", BorderLayout.WEST);
        panel.add(myFilePattern);
        myFilePath = LabeledComponent.create(myFilePathField, "File", BorderLayout.WEST);
        panel.add(myFilePath);
        reloadSpecificItems();
    }

    private void reloadSpecificItems() {
        myModuleName.setVisible(Objects.equals(myKindField.getSelectedItem(), RakUTestKind.MODULE));
        myDirectoryPath.setVisible(Objects.equals(myKindField.getSelectedItem(), RakUTestKind.DIRECTORY));
        myFilePattern.setVisible(Objects.equals(myKindField.getSelectedItem(), RakUTestKind.PATTERN));
        myFilePath.setVisible(Objects.equals(myKindField.getSelectedItem(), RakUTestKind.FILE));
    }

    private static class RakuModuleModel implements ComboBoxModel<String> {
        private final List<String> moduleNames = new ArrayList<>();
        private String current;

        private RakuModuleModel(Project project) {
            for (Module module : ModuleManager.getInstance(project).getModules()) {
                if (ModuleType.get(module) instanceof Perl6ModuleType) {
                    moduleNames.add(module.getName());
                }
            }
            project.getMessageBus().connect().subscribe(ProjectTopics.MODULES, new ModuleListener() {
                @Override
                public void moduleAdded(@NotNull Project project, @NotNull Module module) {
                    moduleNames.add(module.getName());
                }

                @Override
                public void moduleRemoved(@NotNull Project project, @NotNull Module module) {
                    moduleNames.remove(module.getName());
                    if (current.equals(module.getName()))
                        current = null;
                }

                @Override
                public void modulesRenamed(@NotNull Project project,
                                           @NotNull List<Module> modules,
                                           @NotNull Function<Module, String> oldNameProvider) {
                    for (Module module : modules) {
                        String oldName = oldNameProvider.fun(module);
                        if (oldName.equals(current))
                            current = module.getName();
                        for (int i = 0; i < moduleNames.size(); i++) {
                            if (moduleNames.get(i).equals(oldName))
                                moduleNames.set(i, module.getName());
                        }
                    }
                }
            });
        }

        @Override
        public void setSelectedItem(Object anItem) {
            current = (String)anItem;
        }

        @Override
        public Object getSelectedItem() {
            return current;
        }

        @Override
        public int getSize() {
            return moduleNames.size();
        }

        @Override
        public String getElementAt(int index) {
            return moduleNames.get(index);
        }

        @Override
        public void addListDataListener(ListDataListener l) {}

        @Override
        public void removeListDataListener(ListDataListener l) {}
    }
}
