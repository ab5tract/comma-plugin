package edument.perl6idea.module;

import com.intellij.ide.projectWizard.ProjectCategory;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import org.jetbrains.annotations.NotNull;

public class Perl6ModuleTypeCategory extends ProjectCategory {
    @NotNull
    @Override
    public ModuleBuilder createModuleBuilder() {
        return new Perl6ModuleBuilder();
    }
}
