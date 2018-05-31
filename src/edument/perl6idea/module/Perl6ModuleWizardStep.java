package edument.perl6idea.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import edument.perl6idea.utils.Patterns;
import edument.perl6idea.utils.Perl6ProjectType;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

class Perl6ModuleWizardStep extends ModuleWizardStep {
    private final Perl6ModuleBuilder builder;
    private JPanel myMainPanel;
    private JTextField myScriptName;
    private JTextField myModuleName;
    private JTextField myEntryName;
    private Perl6ProjectType currentType;

    Perl6ModuleWizardStep(Perl6ModuleBuilder builder) {
        this.builder = builder;
        this.currentType = builder.getType();
        updateInputs();
    }

    private void updateInputs() {
        myMainPanel = new JBPanel<>();
        Border border = myMainPanel.getBorder();
        Border margin = JBUI.Borders.empty(10);
        myMainPanel.setBorder(new CompoundBorder(border, margin));
        myMainPanel.setLayout(new MigLayout());
        switch (builder.getType()) {
            case PERL6_SCRIPT:
                myScriptName = new JBTextField(40);
                LabeledComponent<JTextField> scriptName = LabeledComponent.create(myScriptName, "Script name", BorderLayout.WEST);
                myMainPanel.add(scriptName);
                break;
            case PERL6_MODULE: {
                myModuleName = new JBTextField(40);
                LabeledComponent<JTextField> moduleName = LabeledComponent.create(myModuleName, "Module name", BorderLayout.WEST);
                myMainPanel.add(moduleName);
                break;
            }
            default: {
                myModuleName = new JBTextField(40);
                myMainPanel.add(new JLabel("Module name"));
                myMainPanel.add(myModuleName, "wrap");
                myEntryName = new JBTextField(40);
                myMainPanel.add(new JLabel("Entry point name"));
                myMainPanel.add(myEntryName);
                break;
            }
        }
    }

    @Override
    public JComponent getComponent() {
        // If the module type was changed, we need to re-draw UI
        if (builder.getType() != currentType) {
            currentType = builder.getType();
            updateInputs();
        }
        return myMainPanel;
    }

    @Override
    public boolean validate() throws ConfigurationException {
        switch (builder.getType()) {
            case PERL6_SCRIPT:
                checkScriptName();
                break;
            case PERL6_MODULE:
                checkModuleName();
                break;
            default:
                checkModuleName();
                checkEntryPointName();
                break;
        }
        return true;
    }

    private void checkEntryPointName() throws ConfigurationException {
        String name = myEntryName.getText();
        if (!name.matches(Patterns.ENTRY_POINT_PATTERN)) {
            throw new ConfigurationException("Entry point name is incorrect");
        }
    }

    private void checkModuleName() throws ConfigurationException {
        String name = myModuleName.getText();
        if (!name.matches(Patterns.MODULE_PATTERN)) {
            throw new ConfigurationException("Module name is incorrect");
        }
    }

    private void checkScriptName() throws ConfigurationException {
        String name = myScriptName.getText();
        if (!name.matches(Patterns.SCRIPT_PATTERN)) {
            throw new ConfigurationException("Script name must contain only letters and numbers");
        }
    }

    @Override
    public void updateDataModel() {
        switch (builder.getType()) {
            case PERL6_SCRIPT:
                builder.setScriptName(myScriptName.getText());
                break;
            case PERL6_MODULE:
                builder.setModuleName(myModuleName.getText());
                break;
            case PERL6_APPLICATION:
                builder.setModuleName(myModuleName.getText());
                builder.setEntryPointName(myEntryName.getText());
                break;
        }
    }
}
