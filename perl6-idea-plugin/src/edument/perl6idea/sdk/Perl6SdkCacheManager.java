package edument.perl6idea.sdk;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import edument.perl6idea.module.Perl6ModuleType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Service
public class Perl6SdkCacheManager implements ProjectManagerListener {
    @Override
    public void projectClosed(@NotNull Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            if (Objects.equals(module.getModuleTypeName(), Perl6ModuleType.getInstance().getId())) {
                Perl6SdkType.getInstance().invalidateCaches(project);
                return;
            }
        }
    }
}
