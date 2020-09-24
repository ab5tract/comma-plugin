package edument.perl6idea.sdk;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

@Service
public class Perl6SdkCacheManager implements ProjectManagerListener {
    @Override
    public void projectClosed(@NotNull Project project) {
        Perl6SdkType.getInstance().invalidateFileCaches(project);
    }
}
