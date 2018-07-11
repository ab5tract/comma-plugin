package edument.perl6idea.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.ui.JBColor;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ui.JBUI;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModuleMetaEditor implements ModuleConfigurationEditor {
    public static final String MISSING_FIELD_MESSAGE = "Does not exist, must be set!";
    private final Module myModule;
    private JComponent mySettingsPanel;
    private JTextField myNameField;
    private JTextField myVersionField;
    private JTextField myDescriptionField;
    private JTextField myAuthField;
    private JTextField myLicenseField;
    private Map<String, String> myMeta = new HashMap<>();
    private static String[] keys = new String[]{
      "name", "version", "auth", "description", "license"
    };
    private Set<String> myMissingFields = new HashSet<>();
    private JSONObject myMetaProps;
    private Set<String> myEmptyFields = new HashSet<>();

    public ModuleMetaEditor(ModuleConfigurationState state) {
        myModule = state.getRootModel().getModule();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Metadata";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (mySettingsPanel == null) {
            mySettingsPanel = new JPanel(new MigLayout());
            createFields();
        }
        return mySettingsPanel;
    }

    private void createFields() {
        mySettingsPanel.add(new JLabel("Name:"));
        myNameField = new JTextField(80);
        addListener(myNameField, "name");
        mySettingsPanel.add(myNameField, "wrap");

        mySettingsPanel.add(new JLabel("Description:"));
        myDescriptionField = new JTextField(80);
        addListener(myDescriptionField, "description");
        mySettingsPanel.add(myDescriptionField, "wrap");

        mySettingsPanel.add(new JLabel("Version:"));
        myVersionField = new JTextField(80);
        addListener(myVersionField, "version");
        mySettingsPanel.add(myVersionField, "wrap");

        mySettingsPanel.add(new JLabel("Auth:"));
        myAuthField = new JTextField(80);
        addListener(myAuthField, "auth");
        mySettingsPanel.add(myAuthField, "wrap");

        mySettingsPanel.add(new JLabel("License:"));
        myLicenseField = new JTextField(80);
        addListener(myLicenseField, "license");
        mySettingsPanel.add(myLicenseField, "wrap");
    }

    private void addListener(JTextField field, String key) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkValue();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkValue();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkValue();
            }

            private void checkValue() {
                if (field.getText().isEmpty())
                    myEmptyFields.add(key);
                else
                    myEmptyFields.remove(key);

                if (field.getText().equals(MISSING_FIELD_MESSAGE))
                    myMissingFields.add(key);
                else
                    myMissingFields.remove(key);
            }
        });
    }

    @Override
    public boolean isModified() {
        return !gatherFields().equals(myMeta);
    }

    @Override
    public void reset() {
        Path meta = Paths.get(myModule.getModuleFilePath()).resolveSibling("META6.json");
        if (!Files.exists(meta)) {
            String path = myModule.getProject().getBasePath();
            if (path == null) return;
            meta = Paths.get(path, "META6.json");
            if (!Files.exists(meta)) return;
        }
        String jsonString;
        try {
            jsonString = new String(Files.readAllBytes(meta), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return;
        }
        myMetaProps = new JSONObject(jsonString);
        for (String field : keys) {
            try {
                myMeta.put(field, myMetaProps.getString(field));
            }
            catch (JSONException e) {
                myMissingFields.add(field);
                myMeta.put(field, MISSING_FIELD_MESSAGE);
            }
        }
        populateFields();
    }

    private void populateFields() {
        myNameField.setText(myMeta.get("name"));
        myDescriptionField.setText(myMeta.get("description"));
        myVersionField.setText(myMeta.get("version"));
        myAuthField.setText(myMeta.get("auth"));
        myLicenseField.setText(myMeta.get("license"));
    }

    private Map<String, String> gatherFields() {
        Map<String, String> fields = new HashMap<>();
        fields.put("name", myNameField.getText());
        fields.put("description", myDescriptionField.getText());
        fields.put("version", myVersionField.getText());
        fields.put("auth", myAuthField.getText());
        fields.put("license", myLicenseField.getText());
        return fields;
    }

    @Override
    public void apply() throws ConfigurationException {
        // META6.json exists, but has missing fields
        if (!myMissingFields.isEmpty())
            throw new ConfigurationException(
              "Missing fields are present: " +
              String.join(", ", myMissingFields));
        // META6.json does not exist at all
        myEmptyFields = new HashSet<>();
        gatherFields().forEach((k, v) -> {
            if (v.isEmpty()) myEmptyFields.add(k);
        });
        if (!myEmptyFields.isEmpty())
            throw new ConfigurationException(
              "Empty fields: " +
              String.join(", ", ArrayUtil.toStringArray(myEmptyFields)));
        saveFields();
    }

    private void saveFields() {
        myMetaProps.put("name", myNameField.getText());
        myMetaProps.put("description", myDescriptionField.getText());
        myMetaProps.put("auth", myAuthField.getText());
        myMetaProps.put("version", myVersionField.getText());
        myMetaProps.put("license", myLicenseField.getText());
        Path path = Paths.get(myModule.getModuleFilePath()).resolveSibling("META6.json");
        Perl6ModuleBuilder.writeMetaFile(path, myMetaProps);
    }
}
