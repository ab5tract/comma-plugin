package org.raku.run;

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
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.*;
import java.util.List;

public class RakuRunSettingsEditor extends SettingsEditor<RakuRunConfiguration> {
    private static final String[] LOG_TIMELINE_EVENT_TYPES = {"await", "file", "process", "socket", "start", "thread"};
    private final Project myProject;
    private TextFieldWithBrowseButton fileField;
    private CommonProgramParametersPanel myParams;
    private JTextField myDebugPort;
    private JCheckBox toStartSuspended;
    private RawCommandLineEditor myRakuParametersPanel;
    private JBList<String> myLogTimelineOptions;

    RakuRunSettingsEditor(Project project) {
        super();
        myProject = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull RakuRunConfiguration conf) {
        fileField.setText(conf.getScriptPath());
        if (conf.getDebugPort() == 0) {
            myDebugPort.setText(String.valueOf(9999));
        }
        else {
            myDebugPort.setText(String.valueOf(conf.getDebugPort()));
        }
        toStartSuspended.setSelected(conf.isStartSuspended());
        if (conf.getInterpreterParameters() == null) {
            myRakuParametersPanel.setText("");
        }
        else {
            myRakuParametersPanel.setText(conf.getInterpreterParameters());
        }
        myParams.reset(conf);
        if (conf.getWorkingDirectory() == null) {
            myParams.setWorkingDirectory(myProject.getBasePath());
        }
        else {
            myParams.setWorkingDirectory(conf.getWorkingDirectory());
        }
        String events = conf.getLogTimelineEvents();
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < LOG_TIMELINE_EVENT_TYPES.length; i++) {
            if (events.contains(LOG_TIMELINE_EVENT_TYPES[i]))
                indexes.add(i);
        }
        myLogTimelineOptions.setSelectedIndices(indexes.stream().mapToInt(i->i).toArray());
    }

    @Override
    protected void applyEditorTo(@NotNull RakuRunConfiguration conf) throws ConfigurationException {
        String fileLine = fileField.getText();
        if (Objects.equals(fileLine, "")) throw new ConfigurationException("Main script path is absent");

        String workingDir = myParams.getWorkingDirectoryAccessor().getText();
        if (workingDir.endsWith("/")) {
            workingDir = workingDir.substring(0, workingDir.length() - 1);
        }

        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(workingDir + "/" + fileLine);
        if (file == null || ! file.exists()) {
            VirtualFile revisedPath = LocalFileSystem.getInstance().findFileByPath(workingDir + "/" + fileLine);
            if (revisedPath == null || ! revisedPath.exists()) {
                throw new ConfigurationException("Main script path is incorrect");
            }
        } else {
            conf.setScriptPath(fileLine);
        }
        conf.setDebugPort(Integer.parseInt(myDebugPort.getText()));
        conf.setStartSuspended(toStartSuspended.isSelected());
        conf.setInterpreterParameters(myRakuParametersPanel.getText());
        myParams.applyTo(conf);
        // Log::Timeline event types
        int[] event_indexes = myLogTimelineOptions.getSelectedIndices();
        StringJoiner eventTypeString = new StringJoiner(",");
        for (int i = 0; i < LOG_TIMELINE_EVENT_TYPES.length; i++) {
            int finalI = i;
            if (Arrays.stream(event_indexes).anyMatch(el -> el == finalI)) {
                eventTypeString.add(LOG_TIMELINE_EVENT_TYPES[i]);
            }
        }
        conf.setLogTimelineEvents(eventTypeString.toString());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JComponent mainTab = getMainTab();
        JComponent debugTab = getDebugTab();
        JBTabbedPane tabbedPaneWrapper = new JBTabbedPane();
        tabbedPaneWrapper.addTab("Main", mainTab);
        tabbedPaneWrapper.addTab("Debug", debugTab);
        return tabbedPaneWrapper;
    }

    @NotNull
    protected JComponent getMainTab() {
        FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(
            true, false,
            false, false,
            false, false) {
            @Override
            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                return file.isDirectory() ||
                       file.getExtension() == null
                       ||
                       Arrays.asList("pm6", "pl6", "p6", "t", "rakumod", "raku", "rakutest", "rakudoc", "").contains(file.getExtension());
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
                myRakuParametersPanel = new RawCommandLineEditor();
                myPerl6ParametersComponent =
                    LabeledComponent.create(myRakuParametersPanel, "Raku parameters", BorderLayout.WEST);
                add(myPerl6ParametersComponent);
                myLogTimelineOptions = new JBList<>(LOG_TIMELINE_EVENT_TYPES);
                myLogTimelineOptions.setCellRenderer(new ListCellRenderer<>() {
                    private final ListCellRenderer<Object> renderer = new DefaultListCellRenderer();

                    @Override
                    public Component getListCellRendererComponent(JList<? extends String> list,
                                                                  String value,
                                                                  int index,
                                                                  boolean isSelected,
                                                                  boolean cellHasFocus) {
                        String text;
                        switch (value) {
                            case "await":
                                text = "Outstanding await expressions";
                                break;
                            case "file":
                                text = "File I/O";
                                break;
                            case "process":
                                text = "Process Spawning";
                                break;
                            case "socket":
                                text = "Sockets";
                                break;
                            case "start":
                                text = "Scheduled/running start blocks";
                                break;
                            case "thread":
                            default:
                                text = "Threads";
                                break;
                        }
                        return renderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
                    }
                });
                DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
                myLogTimelineOptions.setSelectionModel(selectionModel);
                LabeledComponent<?> logTimelineComponent = LabeledComponent.create(
                    myLogTimelineOptions, "Log::Timeline events", BorderLayout.WEST);
                add(logTimelineComponent);
            }

            @Override
            protected void setupAnchor() {
                List<Component> components = new ArrayList<>();
                components.add(myFileComponent);
                components.addAll(Arrays.asList(super.getComponents()));
                components.add(myPerl6ParametersComponent);
                myAnchor = UIUtil.mergeComponentsWithAnchor(
                    components.toArray(new PanelWithAnchor[0]));
            }
        };
        myParams.setProgramParametersLabel("Script parameters:");
        myParams.setWorkingDirectory(myProject.getBasePath());
        return myParams;
    }

    @NotNull
    protected JComponent getDebugTab() {
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
                        }
                        catch (NumberFormatException ignored) {
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
