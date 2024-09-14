package org.raku.comma.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.Disposable;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import org.raku.comma.utils.RakuProjectType;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class TypeWizardStepForm extends ModuleWizardStep implements Disposable {
    private final boolean isCreatingNewProject;
    private JPanel basePanel;
    private JList<String> list1;
    private JLabel description;
    private JPanel panel1;
    private JPanel panel2;
    private final RakuModuleBuilder builder;

    public TypeWizardStepForm(RakuModuleBuilder builder, boolean isCreatingNewProject) {
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
        builder.setRakuModuleType(RakuProjectType.fromTypeLabel(selectedType));
    }

    public void createUIComponents() {
        basePanel = new JBPanel<>();
        list1 = new JBList<>();
        description = new JBLabel();
        panel1 = new JPanel();
        panel1.setBorder(new TitledBorder(isCreatingNewProject ? "Project Type" : "Module Type"));
        list1.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            RakuProjectType type = RakuProjectType.fromTypeLabel(getSelectedType());
            builder.setRakuModuleType(type);
            description.setText("<html>" + RakuProjectType.getDescription(type) + "</html>");
        });
    }
}
