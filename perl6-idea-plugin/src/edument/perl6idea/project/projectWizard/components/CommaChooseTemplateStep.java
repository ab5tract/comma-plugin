// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.project.projectWizard.components;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author Dmitry Avdeev
 */
public class CommaChooseTemplateStep extends ModuleWizardStep {
    private final WizardContext myWizardContext;
    private final CommaProjectTypeStep myProjectTypeStep;

    private JPanel myPanel;
    private ProjectTemplateList myTemplateList;
    private JBCheckBox myCreateFromTemplateCheckBox;

    public CommaChooseTemplateStep(WizardContext wizardContext, CommaProjectTypeStep projectTypeStep) {
        myWizardContext = wizardContext;
        myProjectTypeStep = projectTypeStep;
        myCreateFromTemplateCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myTemplateList.setEnabled(myCreateFromTemplateCheckBox.isSelected());
                if (myCreateFromTemplateCheckBox.isSelected()) {
                    IdeFocusManager.getGlobalInstance()
                        .doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(myTemplateList.getList(), true));
                }
            }
        });
        myTemplateList.setEnabled(false);
    }

    @Override
    public boolean isStepVisible() {
        return myWizardContext.isCreatingNewProject() && !myProjectTypeStep.getAvailableTemplates().isEmpty();
    }

    @Override
    public JComponent getComponent() {
        return myPanel;
    }

    @Override
    public void updateStep() {
        myTemplateList.setTemplates(new ArrayList<>(myProjectTypeStep.getAvailableTemplates()), false);
    }

    @Override
    public void updateDataModel() {
        myWizardContext.setProjectTemplate(myCreateFromTemplateCheckBox.isSelected() ? myTemplateList.getSelectedTemplate() : null);
    }

    public ProjectTemplateList getTemplateList() {
        return myTemplateList;
    }

    @TestOnly
    public boolean setSelectedTemplate(String name) {
        myCreateFromTemplateCheckBox.setSelected(true);
        return myTemplateList.setSelectedTemplate(name);
    }
}
