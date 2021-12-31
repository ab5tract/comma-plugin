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
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.util.Function;
import edument.perl6idea.module.Perl6ModuleType;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Perl6TestSettingsEditor extends SettingsEditor<Perl6TestRunConfiguration> {
    protected Project myProject;
    protected JTextField myDegreeField;
    protected RawCommandLineEditor myPerl6ParametersPanel;
    protected EnvironmentVariablesComponent myEnvVariablesField;
    // Specific controls
    protected JComboBox<RakuTestKind> myKindField;
    protected JComboBox<String> myModuleNameField;
    protected TextFieldWithBrowseButton myDirectoryPathField;
    protected JTextField myFilePatternField;
    protected TextFieldWithBrowseButton myFilePathField;
    private final JLabel myTestKindLabel = new JLabel("Test kind");
    private final JLabel myModuleNameLabel = new JLabel("Module");
    private final JLabel myDirectoryPathLabel = new JLabel("Directory");
    private final JLabel myFilePathLabel = new JLabel("File");

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
        myFilePatternField.setText(configuration.getTestPattern());
        myFilePathField.setText(configuration.getFilePath());
        // Generic options
        myDegreeField.setText(String.valueOf(configuration.getParallelismDegree()));
        myEnvVariablesField.setEnvs(configuration.getEnvs());
        myEnvVariablesField.setPassParentEnvs(configuration.isPassParentEnvs());
        myPerl6ParametersPanel.setText(configuration.getInterpreterArguments());
    }

    @Override
    protected void applyEditorTo(@NotNull Perl6TestRunConfiguration configuration) throws ConfigurationException {
        // Specific options
        configuration.setTestKind((RakuTestKind)myKindField.getSelectedItem());
        switch ((RakuTestKind)Objects.requireNonNull(myKindField.getSelectedItem())) {
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
        configuration.setInterpreterArguments(myPerl6ParametersPanel.getText());
        configuration.setTestPattern(myFilePatternField.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JPanel panel = new JPanel(new MigLayout("", "[][grow]", ""));

        // Create controls
        myKindField = new ComboBox<>(RakuTestKind.values());
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
        PromptSupport.setPrompt("unit-*;integration-*-32bit", myFilePatternField);

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
        myPerl6ParametersPanel = new RawCommandLineEditor();
        myEnvVariablesField = new EnvironmentVariablesComponent();

        setupSpecificComponents(panel);
        panel.add(new JLabel("Pattern"), "align label");
        panel.add(myFilePatternField, "wrap, growx");
        panel.add(new JLabel("Tests run in parallel"), "align label");
        panel.add(myDegreeField, "wrap, growx");
        panel.add(new JLabel("Raku parameters"), "align label");
        panel.add(myPerl6ParametersPanel, "wrap, growx");
        panel.add(new JLabel("Environment variables:"), "align label");
        panel.add(myEnvVariablesField.getComponent(), "growx");

        return panel;
    }

    private void setupSpecificComponents(JPanel panel) {
        panel.add(myTestKindLabel, "align label");
        panel.add(myKindField, "wrap, growx");

        panel.add(myModuleNameLabel, "align label, hidemode 3");
        panel.add(myModuleNameField, "wrap, growx, hidemode 3");
        panel.add(myDirectoryPathLabel, "align label, hidemode 3");
        panel.add(myDirectoryPathField, "wrap, growx, hidemode 3");
        panel.add(myFilePathLabel, "align label, hidemode 3");
        panel.add(myFilePathField, "wrap, growx, hidemode 3");

        reloadSpecificItems();
    }

    private void reloadSpecificItems() {
        myModuleNameLabel.setVisible(Objects.equals(myKindField.getSelectedItem(), RakuTestKind.MODULE));
        myModuleNameField.setVisible(Objects.equals(myKindField.getSelectedItem(), RakuTestKind.MODULE));
        myDirectoryPathLabel.setVisible(Objects.equals(myKindField.getSelectedItem(), RakuTestKind.DIRECTORY));
        myDirectoryPathField.setVisible(Objects.equals(myKindField.getSelectedItem(), RakuTestKind.DIRECTORY));
        myFilePathLabel.setVisible(Objects.equals(myKindField.getSelectedItem(), RakuTestKind.FILE));
        myFilePathField.setVisible(Objects.equals(myKindField.getSelectedItem(), RakuTestKind.FILE));
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
                                           @NotNull List<? extends Module> modules,
                                           @NotNull Function<? super Module, String> oldNameProvider) {
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
