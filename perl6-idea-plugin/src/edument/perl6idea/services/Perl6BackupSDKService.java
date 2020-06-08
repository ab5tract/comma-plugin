package edument.perl6idea.services;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
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
public class Perl6BackupSDKService implements PersistentStateComponent<Perl6BackupSDKService> {
    public Map<String, String> projectSdkPaths = new HashMap<>();

    @Nullable
    @Override
    public Perl6BackupSDKService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull Perl6BackupSDKService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public void setProjectSdkPath(String projectFilePath, String sdkPath) {
        projectSdkPaths.put(projectFilePath, sdkPath);
    }

    public String getProjectSdkPath(String projectFilePath) {
        return projectSdkPaths.get(projectFilePath);
    }
}
