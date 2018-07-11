package edument.perl6idea.module;

import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.impl.ModuleTypeManagerImpl;

public class Perl6ModuleTypeManager extends ModuleTypeManagerImpl {
    @Override
    public ModuleType getDefaultModuleType() {
      return new Perl6ModuleType();
    }
}
