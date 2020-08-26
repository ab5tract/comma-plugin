package edument.perl6idea.services;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.components.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.util.xmlb.XmlSerializerUtil;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * This service provides means to work with "secondary SDK".
 * When the Raku plugin is used in products such as IDEA or Webstorm,
 * setting Raku SDK is either impossible or interferes with main project code.
 * To workaround this, we provide some UI to the user to set specific Raku SDK
 * for a project. This service maintains state of this SDK and can be used to set/obtain
 * such "secondary" SDK for Raku parts.
 */
@Service
@State(name = "Raku.Backup.Sdk", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class Perl6BackupSDKService implements PersistentStateComponent<Perl6BackupSDKService.State> {
    private final Project myProject;
    State myState;

    public Perl6BackupSDKService(Project project) {
        myProject = project;
    }

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public void setProjectSdkPath(String projectFilePath, String sdkPath) {
        myState.projectSdkPaths.put(projectFilePath, sdkPath);

        Perl6SdkType instance = Perl6SdkType.getInstance();
        String versionString = instance.getVersionString(sdkPath);

        // If this Sdk is not registered in global project jdk table, we have to add that
        // so that project-less things like Raku VFS still could find them.
        if (versionString != null) {
            @Nullable Sdk sdk = ProjectJdkTable.getInstance().findJdk(versionString);
            if (sdk == null) {
                Sdk secondarySdk = ProjectJdkTable.getInstance().createSdk(versionString, instance);
                WriteAction.run(() -> ProjectJdkTable.getInstance().addJdk(secondarySdk));
            }
        }

        // The secondary SDK was set, invalidate caches
        instance.invalidateCaches();
        for (Module module : ModuleManager.getInstance(myProject).getModules()) {
            Perl6MetaDataComponent component = module.getService(Perl6MetaDataComponent.class);
            if (component != null)
                component.triggerMetaBuild();
        }
    }

    public String getProjectSdkPath(String projectFilePath) {
        return myState.projectSdkPaths.get(projectFilePath);
    }

    public static class State {
        public Map<String, String> projectSdkPaths;

        public State() {
            projectSdkPaths = new HashMap<>();
        }
    }
}
