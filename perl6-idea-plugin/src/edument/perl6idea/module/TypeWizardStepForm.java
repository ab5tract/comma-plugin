package edument.perl6idea.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.Disposable;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import edument.perl6idea.utils.Perl6ProjectType;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class TypeWizardStepForm extends ModuleWizardStep implements Disposable {
    private final boolean isCreatingNewProject;
    private JPanel basePanel;
    private JList<String> list1;
    private JLabel description;
    private JPanel panel1;
    private JPanel panel2;
    private final Perl6ModuleBuilder builder;

    public TypeWizardStepForm(Perl6ModuleBuilder builder, boolean isCreatingNewProject) {
        this.builder = builder;
        this.isCreatingNewProject = isCreatingNewProject;
    }

    @Override
    public void dispose() {}

    @Override
    public JComponent getComponent() {
        return basePanel;
    }

    private String getSelectedType() {
        return list1.getSelectedValue();
    }

    @Override
    public void updateDataModel() {
        String selectedType = getSelectedType();
        builder.setPerl6ModuleType(Perl6ProjectType.fromTypeLabel(selectedType));
    }

    public void createUIComponents() {
        basePanel = new JBPanel<>();
        list1 = new JBList<>();
        description = new JBLabel();
        panel1 = new JPanel();
        panel1.setBorder(new TitledBorder(isCreatingNewProject ? "Project Type" : "Module Type"));
        list1.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            Perl6ProjectType type = Perl6ProjectType.fromTypeLabel(getSelectedType());
            builder.setPerl6ModuleType(type);
            description.setText("<html>" + Perl6ProjectType.getDescription(type) + "</html>");
        });
    }
}
