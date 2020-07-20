package edument.perl6idea.event;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectExtension;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SdkChangeListener extends ProjectExtension {
    private final Project myProject;

    public SdkChangeListener(Project project) {
        myProject = project;
    }

    @Override
    public void projectSdkChanged(@Nullable Sdk sdk) {
        if (!ApplicationManager.getApplication().isUnitTestMode()) {
            Perl6SdkType.getInstance().invalidateCaches();
            for (Module module : ModuleManager.getInstance(myProject).getModules()) {
                Perl6MetaDataComponent component = module.getService(Perl6MetaDataComponent.class);
                if (component != null)
                    component.triggerMetaBuild();
            }
        }
    }

    @Override
    public void readExternal(@NotNull Element element) {
    }

    @Override
    public void writeExternal(@NotNull Element element) {
    }
}
