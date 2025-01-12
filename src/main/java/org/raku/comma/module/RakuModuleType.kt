package org.raku.comma.module

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import org.raku.comma.RakuIcons
import org.raku.comma.project.wizard.RakuModuleWizardBuilder
import javax.swing.Icon

class RakuModuleType : ModuleType<RakuModuleWizardBuilder?>(ID) {
    override fun createModuleBuilder(): RakuModuleWizardBuilder {
        return RakuModuleWizardBuilder()
    }

    override fun getName(): String {
        return "Raku Module"
    }

    override fun getDescription(): String {
        return "Raku modules are used for developing <b>Raku</b> applications."
    }

    val bigIcon: Icon
        get() = RakuIcons.CAMELIA

    override fun getNodeIcon(b: Boolean): Icon {
        return RakuIcons.CAMELIA
    }

    //    @Nullable
    //    @Override
    //    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep, @NotNull final ModuleBuilder moduleBuilder) {
    //        return new SdkSettingsStep(settingsStep.getContext());
    //    }
    override fun createWizardSteps(
        wizardContext: WizardContext,
        moduleBuilder: RakuModuleWizardBuilder,
        modulesProvider: ModulesProvider
    ): Array<ModuleWizardStep?> {
        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider)
    }

    companion object {
        const val ID: String = "RAKU_MODULE_TYPE"

        @JvmStatic
        val instance: RakuModuleType
            get() = ModuleTypeManager.getInstance().findByID(ID) as RakuModuleType
    }
}
