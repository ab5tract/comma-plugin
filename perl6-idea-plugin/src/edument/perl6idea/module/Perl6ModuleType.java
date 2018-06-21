package edument.perl6idea.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SdkSettingsStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.util.Condition;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6ModuleType extends ModuleType<Perl6ModuleBuilder> {
    private static final String ID = "PERL6_MODULE_TYPE";

    public Perl6ModuleType() {
        super(ID);
    }

    @NotNull
    public static Perl6ModuleType getInstance() {
        return (Perl6ModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public Perl6ModuleBuilder createModuleBuilder() {
        return new Perl6ModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Perl 6 Module";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Perl 6 modules are used for developing <b>Perl 6</b> applications.";
    }

    public Icon getBigIcon() {
        return Perl6Icons.CAMELIA;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return Perl6Icons.CAMELIA;
    }

    @Nullable
    @Override
    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep, @NotNull final ModuleBuilder moduleBuilder) {
        final Condition<SdkTypeId> condition = moduleBuilder::isSuitableSdkType;
        return new SdkSettingsStep(settingsStep, moduleBuilder, condition);
    }
}
