package edument.perl6idea.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class Perl6ModuleListStarter implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        Perl6ModuleListFetcher.refreshModules(project);
    }
}
