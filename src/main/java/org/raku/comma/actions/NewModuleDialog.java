package org.raku.comma.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.ArrayUtil;
import org.raku.comma.utils.Patterns;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class NewModuleDialog extends DialogWrapper {
    private final String myLibPath;
    private JTextField moduleNameField;
    private JComboBox<String> moduleTypeComboBox;
    private JPanel myPanel;
    private JCheckBox myIsUnitScopedCheckBox;

    public NewModuleDialog(@Nullable Project project,
                           String libPath,
                           String prefix) {
        super(project, false);
        myLibPath = formLibPath(libPath, prefix);
        init();
        setTitle("New Raku Module");
        moduleNameField.setText(prefix);
    }

    private static String formLibPath(String path, String prefix) {
        Path tmpPath = Paths.get(path);

        String[] prefixParts = prefix.split("::");

        for (int i = prefixParts.length - 1; i >= 0; i--) {
            String prefixPart = prefixParts[i];
            if (Objects.equals(tmpPath.toFile().getName(), prefixPart)) {
                tmpPath = tmpPath.getParent();
            }
        }
        return tmpPath.toString();
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return moduleNameField;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String newModuleName = moduleNameField.getText();
        boolean isCorrectModuleName = newModuleName.matches(Patterns.MODULE_PATTERN);
        if (!isCorrectModuleName)
            return new ValidationInfo("Incorrect module name (examples: `Module`, `Foo::Bar`)", moduleNameField);
        String[] parts = newModuleName.split("::");
        String[] prefix = parts.length > 1 ? Arrays.copyOfRange(parts, 0, parts.length - 1) : ArrayUtil.EMPTY_STRING_ARRAY;
        String name = parts[parts.length - 1];
        Path directoryFile = Paths.get(myLibPath, prefix);

        if (directoryFile.resolve(name + ".pm6").toFile().exists() ||
            directoryFile.resolve(name + ".rakumod").toFile().exists())
            return new ValidationInfo("Module + " + newModuleName + " already exists", moduleNameField);

        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        moduleTypeComboBox.addItem("Empty");
        moduleTypeComboBox.addItem("Class");
        moduleTypeComboBox.addItem("Role");
        moduleTypeComboBox.addItem("Grammar");
        moduleTypeComboBox.addItem("Monitor");
        moduleTypeComboBox.addItem("Module");
        moduleTypeComboBox.addItem("Model");
        return myPanel;
    }

    public String getModuleName() {
        return moduleNameField.getText();
    }

    public String getModuleType() {
        return (String)moduleTypeComboBox.getSelectedItem();
    }

    public boolean isUnitModule() {
        return myIsUnitScopedCheckBox.isSelected();
    }
}
