package edument.perl6idea.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class Perl6ModuleListStarter implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        // Create service if not yet
        project.getService(Perl6ModuleListFetcher.class);
        Perl6ModuleListFetcher.refreshModules(project);
    }
}
