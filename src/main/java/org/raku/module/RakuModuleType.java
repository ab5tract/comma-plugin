package org.raku.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.util.Condition;
import org.raku.RakuIcons;
import org.raku.project.projectWizard.components.SdkSettingsStep;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@InternalIgnoreDependencyViolation
public class RakuModuleType extends ModuleType<RakuModuleBuilder> {
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
    public RakuModuleBuilder createModuleBuilder() {
        return new RakuModuleBuilder();
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

    @Nullable
    @Override
    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep, @NotNull final ModuleBuilder moduleBuilder) {
        final Condition<SdkTypeId> condition = moduleBuilder::isSuitableSdkType;
        return new SdkSettingsStep(settingsStep, moduleBuilder, condition);
    }
}
