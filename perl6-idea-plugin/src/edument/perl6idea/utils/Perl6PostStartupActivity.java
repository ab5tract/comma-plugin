package edument.perl6idea.utils;

import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class Perl6PostStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(new Task.Backgroundable(project, "Getting Perl 6 Modules List"){
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Perl6ModuleListFetcher.getModulesListAsync(project);
            }
        }, new EmptyProgressIndicator());
    }
}
