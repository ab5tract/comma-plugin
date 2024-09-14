package org.raku.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.util.text.VersionComparatorUtil;
import org.raku.sdk.RakuSdkType;
import org.raku.services.RakuBackupSDKService;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FIXME
 */
public class RakuScriptRunner extends PtyCommandLine {

    public RakuScriptRunner(Project project) throws ExecutionException {
        this(RakuSdkType.getSdkHomeByProject(project));
    }

    protected RakuScriptRunner(@Nullable String sdkHome) throws ExecutionException {
        if (sdkHome == null)
            throw new ExecutionException("No SDK for project");
        if (Paths.get(sdkHome).toFile().isFile())
            setExePath(sdkHome);
        else {
            String perl6Binary = RakuSdkType.findRakuInSdkHome(sdkHome);
            if (perl6Binary == null)
                throw new ExecutionException("SDK is invalid");
            setExePath(perl6Binary);
        }
    }

    public RakuScriptRunner(Project project, int debugPort) throws ExecutionException {
        List<String> parameters = populateDebugCommandLine(project, debugPort);
        if (parameters == null) {
            throw new ExecutionException("SDK is not valid for debugging");
        }
        setExePath(parameters.getFirst());
        addParameters(parameters.subList(1, parameters.size()));
    }

    @Nullable
    private static List<String> populateDebugCommandLine(Project project, int debugPort) {
        List<String> command = new ArrayList<>();
        @Nullable Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        @Nullable String versionString = null;
        @Nullable String homePath = null;
        if (sdk == null) {
            RakuBackupSDKService backupSDKService = project.getService(RakuBackupSDKService.class);
            String backupSDKPath = backupSDKService.getProjectSdkPath(project.getProjectFilePath());
            if (backupSDKPath != null) {
                RakuSdkType sdkType = RakuSdkType.getInstance();
                versionString = sdkType.getVersionString(backupSDKPath);
                homePath = backupSDKPath;
            }
        } else {
            versionString = sdk.getVersionString();
            homePath = sdk.getHomePath();
        }

        if (versionString == null || homePath == null)
            return null;

        if (VersionComparatorUtil.compare(versionString, "v2019.07") >= 0) {
            String rakuBinary = RakuSdkType.findRakuInSdkHome(homePath);
            if (rakuBinary == null) return null;
            command.add(rakuBinary);
            command.add("--debug-port=" + debugPort);
            command.add("--debug-suspend");
        } else {
            Map<String, String> moarBuildConfiguration = RakuSdkType.getInstance().getMoarBuildConfiguration(project);
            if (moarBuildConfiguration == null) return null;
            String prefix = moarBuildConfiguration.getOrDefault("perl6::prefix", null);
            if (prefix == null) {
                prefix = moarBuildConfiguration.getOrDefault("Raku::prefix", "");
            }
            command.add(Paths.get(prefix, "bin", "moar").toString());
            // Always start suspended so we have time to send breakpoints and event handlers.
            // If the option is disabled, we'll resume right after that.
            command.add("--debug-port=" + debugPort);
            command.add("--debug-suspend");
            command.add("--libpath=" + Paths.get(prefix, "share", "nqp", "lib"));
            command.add("--libpath=" + Paths.get(prefix, "share", "perl6", "lib"));
            command.add("--libpath=" + Paths.get(prefix, "share", "perl6", "runtime"));
            command.add(Paths.get(prefix, "share", "perl6", "runtime", "perl6.moarvm").toString());
        }
        return command;
    }
}
