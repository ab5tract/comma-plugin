package edument.perl6idea.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import org.jetbrains.annotations.NotNull;

public class Perl6ModuleListStarter implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        // Create service if not yet
        project.getService(Perl6ModuleListFetcher.class);
        Perl6ModuleListFetcher.refreshModules(project);
        // Initialize metadata listeners
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules)
            module.getService(Perl6MetaDataComponent.class);
    }
}
