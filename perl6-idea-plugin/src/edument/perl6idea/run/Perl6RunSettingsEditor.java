package edument.perl6idea.run;

import com.intellij.execution.ui.CommonProgramParametersPanel;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.PanelWithAnchor;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Perl6RunSettingsEditor extends SettingsEditor<Perl6RunConfiguration> {
    private Project myProject;
    private TextFieldWithBrowseButton fileField;
    private CommonProgramParametersPanel myParams;
    private JTextField myDebugPort;
    private JCheckBox toStartSuspended;
    private RawCommandLineEditor myPerl6ParametersPanel;

    Perl6RunSettingsEditor(Project project) {
        super();
        myProject = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull Perl6RunConfiguration conf) {
        fileField.setText(conf.getScriptPath());
        if (conf.getDebugPort() == 0) {
            myDebugPort.setText(String.valueOf(9999));
        } else {
            myDebugPort.setText(String.valueOf(conf.getDebugPort()));
        }
        toStartSuspended.setSelected(conf.isStartSuspended());
        if (conf.getInterpreterParameters() == null) {
            myPerl6ParametersPanel.setText("");
        } else {
            myPerl6ParametersPanel.setText(conf.getInterpreterParameters());
        }
        myParams.reset(conf);
        if (conf.getWorkingDirectory() == null) {
            myParams.setWorkingDirectory(myProject.getBasePath());
        } else {
            myParams.setWorkingDirectory(conf.getWorkingDirectory());
        }
    }

    @Override
    protected void applyEditorTo(@NotNull Perl6RunConfiguration conf) throws ConfigurationException {
        String fileLine = fileField.getText();
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(fileLine);
        if (file == null || !file.exists()) {
            throw new ConfigurationException("Main script path is incorrect");
        } else if (Objects.equals(fileLine, "")) {
            throw new ConfigurationException("Main script path is absent");
        } else  {
            conf.setScriptPath(fileLine);
        }
        conf.setDebugPort(Integer.parseInt(myDebugPort.getText()));
        conf.setStartSuspended(toStartSuspended.isSelected());
        conf.setInterpreterParameters(myPerl6ParametersPanel.getText());
        myParams.applyTo(conf);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JComponent mainTab = getMainTab();
        JComponent debugTab = getDebugTab();
        if (mainTab != null && debugTab != null) {
            JBTabbedPane tabbedPaneWrapper = new JBTabbedPane();
            tabbedPaneWrapper.addTab("Main", mainTab);
            tabbedPaneWrapper.addTab("Debug", debugTab);
            return tabbedPaneWrapper;
        } else if (mainTab != null) {
            return mainTab;
        } else if (debugTab != null) {
            return debugTab;
        }
        throw new RuntimeException("No components created for settings editor");
    }

    private JComponent getMainTab() {
        FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(
                true, false,
                false, false,
                false, false) {
            @Override
            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                return file.isDirectory() || file.getExtension() == null
                        || Arrays.asList("pm6", "pl6", "p6", "t", "rakumod", "raku", "rakutest", "rakudoc", "").contains(file.getExtension());
            }
        };
        myParams = new CommonProgramParametersPanel() {
            private LabeledComponent<?> myFileComponent;
            private LabeledComponent<RawCommandLineEditor> myPerl6ParametersComponent;

            @Override
            protected void addComponents() {
                fileField = new TextFieldWithBrowseButton();
                fileField.addBrowseFolderListener("Select Script", null, myProject,
                chooserDescriptor);
                myFileComponent = LabeledComponent.create(fileField, "Script", BorderLayout.WEST);
                add(myFileComponent);
                super.addComponents();
                Perl6RunSettingsEditor.this.myPerl6ParametersPanel = new RawCommandLineEditor();
                myPerl6ParametersComponent =
                  LabeledComponent.create(Perl6RunSettingsEditor.this.myPerl6ParametersPanel, "Perl 6 parameters", BorderLayout.WEST);
                add(myPerl6ParametersComponent);
            }

            @Override
            protected void setupAnchor() {
                List<Component> components = new ArrayList<>();
                components.add(myFileComponent);
                components.addAll(Arrays.asList(super.getComponents()));
                components.add(myPerl6ParametersComponent);
                myAnchor = UIUtil.mergeComponentsWithAnchor(
                  components.toArray(new PanelWithAnchor[components.size()]));
            }
        };
        myParams.setProgramParametersLabel("Script parameters:");
        myParams.setWorkingDirectory(myProject.getBasePath());
        return myParams;
    }

    private JComponent getDebugTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 5, true, false));
        myDebugPort = new JTextField(String.valueOf(9999)) {
            @Override
            protected Document createDefaultModel() {
                return new PlainDocument() {
                    @Override
                    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                        if (str == null) return;
                        String oldString = getText(0, getLength());
                        String newString = oldString.substring(0, offs) + str + oldString.substring(offs);
                        try {
                            int newValue = Integer.parseInt(newString);
                            if (newValue < 65536) {
                                super.insertString(offs, str, a);
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                };
            }
        };
        LabeledComponent<JTextField> debugPort = LabeledComponent.create(myDebugPort, "Debug port", BorderLayout.WEST);
        panel.add(debugPort);
        toStartSuspended = new JCheckBox("Start suspended");
        panel.add(toStartSuspended);
        return panel;
    }
}
