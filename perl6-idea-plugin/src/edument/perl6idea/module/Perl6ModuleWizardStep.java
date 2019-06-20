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
    private Perl6ProjectType currentType;
    private JPanel myMainPanel;
    // Script fields
    private JTextField myScriptName;

    // Module fields
    private JTextField myModuleName;

    // Application fields
    private JTextField myEntryName;

    // Cro application fields
    private JCheckBox myWebsocketSupport = new JCheckBox();
    private JCheckBox myTemplatingSUpport = new JCheckBox();

    Perl6ModuleWizardStep(Perl6ModuleBuilder builder) {
        this.builder = builder;
        this.currentType = builder.getPerl6ModuleType();
        updateInputs();
    }

    private void updateInputs() {
        myMainPanel = new JBPanel<>();
        Border border = myMainPanel.getBorder();
        Border margin = JBUI.Borders.empty(10);
        myMainPanel.setBorder(new CompoundBorder(border, margin));
        myMainPanel.setLayout(new MigLayout());
        switch (builder.getPerl6ModuleType()) {
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
            case PERL6_APPLICATION: {
                myModuleName = new JBTextField(40);
                myMainPanel.add(new JLabel("Module name"));
                myMainPanel.add(myModuleName, "wrap");
                myEntryName = new JBTextField(40);
                myMainPanel.add(new JLabel("Entry point name"));
                myMainPanel.add(myEntryName);
                break;
            }
            default: {
                myModuleName = new JBTextField(40);
                myMainPanel.add(new JLabel("Module name"));
                myMainPanel.add(myModuleName, "wrap");
                myMainPanel.add(new JLabel("WebSocket support"));
                myMainPanel.add(myWebsocketSupport, "wrap");
                myMainPanel.add(new JLabel("Templating support"));
                myMainPanel.add(myTemplatingSUpport, "wrap");
                break;
            }
        }
    }

    @Override
    public JComponent getComponent() {
        // If the module type was changed, we need to re-draw UI
        if (builder.getPerl6ModuleType() != currentType) {
            currentType = builder.getPerl6ModuleType();
            updateInputs();
        }
        return myMainPanel;
    }

    @Override
    public boolean validate() throws ConfigurationException {
        switch (builder.getPerl6ModuleType()) {
            case PERL6_SCRIPT:
                checkScriptName();
                break;
            case PERL6_MODULE:
            case CRO_WEB_APPLICATION:
                checkModuleName();
                break;
            case PERL6_APPLICATION:
                checkModuleName();
                checkEntryPointName();
                break;
        }
        return true;
    }

    private void checkEntryPointName() throws ConfigurationException {
        String name = myEntryName.getText();
        if (!name.matches(Patterns.ENTRY_POINT_PATTERN)) {
            throw new ConfigurationException("Entry point name is incorrect. Examples: `foo`, `foo.p6`, `foo.pl6`.");
        }
    }

    private void checkModuleName() throws ConfigurationException {
        String name = myModuleName.getText();
        if (!name.matches(Patterns.MODULE_PATTERN)) {
            throw new ConfigurationException("Module name is incorrect. Examples: `Foo`, `Foo::Bar`, `foo::Bar`");
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
        switch (builder.getPerl6ModuleType()) {
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
            case CRO_WEB_APPLICATION:
                builder.setModuleName(myModuleName.getText());
                builder.setWebsocketSupport(myWebsocketSupport.isSelected());
                builder.setTemplatingSupport(myTemplatingSUpport.isSelected());
                break;
        }
    }
}
