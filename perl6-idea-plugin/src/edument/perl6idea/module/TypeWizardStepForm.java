package edument.perl6idea.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.Disposable;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import edument.perl6idea.utils.Perl6ProjectType;

import javax.swing.*;

public class TypeWizardStepForm extends ModuleWizardStep implements Disposable {
    private JPanel basePanel;
    private JList list1;
    private JLabel description;
    private JPanel panel1;
    private JPanel panel2;
    private Perl6ModuleBuilder builder;

    public TypeWizardStepForm(Perl6ModuleBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void dispose() {
    }

    @Override
    public JComponent getComponent() {
        return basePanel;
    }

    private String getSelectedType() {
        return (String) list1.getSelectedValue();
    }

    @Override
    public void updateDataModel() {
        String selectedType = getSelectedType();
        builder.setType(Perl6ProjectType.fromTypeLabel(selectedType));
    }

    public void createUIComponents() {
        basePanel = new JBPanel<>();
        panel1 = new JBPanel<>();
        panel2 = new JBPanel<>();
        list1 = new JBList();
        description = new JBLabel();
        list1.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            Perl6ProjectType type = Perl6ProjectType.fromTypeLabel(getSelectedType());
            builder.setType(type);
            description.setText("<html>" +  Perl6ProjectType.getDescription(type) + "</html>");
        });
    }
}
