package org.raku.comma.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.raku.comma.RakuIcons;
import org.raku.comma.project.wizard.RakuModuleWizardBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RakuModuleType extends ModuleType<RakuModuleWizardBuilder> {
    public static final String ID = "RAKU_MODULE_TYPE";

    public RakuModuleType() {
        super(ID);
    }

    @NotNull
    public static RakuModuleType getInstance() {
        return (RakuModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public RakuModuleWizardBuilder createModuleBuilder() {
        return new RakuModuleWizardBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Raku Module";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Raku modules are used for developing <b>Raku</b> applications.";
    }

    public Icon getBigIcon() {
        return RakuIcons.CAMELIA;
    }

    @Override
    public @NotNull Icon getNodeIcon(@Deprecated boolean b) {
        return RakuIcons.CAMELIA;
    }

//    @Nullable
//    @Override
//    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep, @NotNull final ModuleBuilder moduleBuilder) {
//        return new SdkSettingsStep(settingsStep.getContext());
//    }

    @Override
    public ModuleWizardStep @NotNull [] createWizardSteps(@NotNull WizardContext wizardContext,
                                                          @NotNull RakuModuleWizardBuilder moduleBuilder,
                                                          @NotNull ModulesProvider modulesProvider)
    {
        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider);
    }
}
