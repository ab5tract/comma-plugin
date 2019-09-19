package edument.perl6idea.sdk;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import edument.perl6idea.module.Perl6ModuleType;

import java.util.Objects;

public class Perl6SdkCacheManager implements ProjectComponent {
    private final Project myProject;

    public Perl6SdkCacheManager(Project project) {
        myProject = project;
    }

    @Override
    public void projectClosed() {
        Module[] modules = ModuleManager.getInstance(myProject).getModules();
        for (Module module : modules) {
            if (Objects.equals(module.getModuleTypeName(), Perl6ModuleType.getInstance().getId())) {
                Perl6SdkType.getInstance().invalidateFileCache();
                return;
            }
        }

    }
}
