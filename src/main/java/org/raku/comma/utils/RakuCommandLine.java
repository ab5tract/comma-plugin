package org.raku.comma.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.util.text.VersionComparatorUtil;
import org.raku.comma.sdk.RakuSdkType;
import org.raku.comma.services.RakuSDKService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A thin wrapper around GeneralCommandLine
 * Features include:
 * * Adds Raku interpreter from Sdk set for the project passed to constructor
 * * Contains a shortcut for executing and gathering output of process
 * Warning: RakuCommandLine usage is *synchronous*. It means that it will block
 * for scripts that take a lot of time to execute and setting execution
 * into separate thread is on the caller side.
 */
public class RakuCommandLine extends GeneralCommandLine {
    private static final Logger LOG = Logger.getInstance(RakuCommandLine.class);

    public RakuCommandLine(Sdk sdk) throws ExecutionException {
        this(sdk.getHomePath());
    }

    public RakuCommandLine(Project project) throws ExecutionException {
        this(RakuSdkType.getSdkHomeByProject(project));
    }

    public RakuCommandLine(@Nullable String sdkHome) throws ExecutionException {
        if (sdkHome == null) throw new ExecutionException("No SDK for project");

        if (Paths.get(sdkHome).toFile().isFile()) {
            setExePath(sdkHome);
        } else {
            String rakuBinary = RakuSdkType.findRakuInSdkHome(sdkHome);
            if (rakuBinary == null) throw new ExecutionException("SDK is invalid");

            setExePath(rakuBinary);
        }
    }

    public RakuCommandLine(Project project, int debugPort) throws ExecutionException {
        List<String> parameters = populateDebugCommandLine(project, debugPort);
        if (parameters == null) {
            throw new ExecutionException("SDK is not valid for debugging");
        }
        setExePath(parameters.getFirst());
        addParameters(parameters.subList(1, parameters.size()));
    }

    @NotNull
    public List<String> executeAndRead(@Nullable File scriptFile) {
        List<String> results = new LinkedList<>();
        try {
            Process p = createProcess();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) results.add(line);
                if (p.waitFor() != 0) {
                    if (scriptFile != null) {
                        if (!scriptFile.delete()) {
                            LOG.warn("Could not delete script file: " + scriptFile.getAbsolutePath());
                        }
                    }
                    return new ArrayList<>();
                }
            } catch (IOException e) {
                LOG.warn(e);
            }
        } catch (InterruptedException | ExecutionException e) {
            LOG.warn(e);
        }
        if (scriptFile != null) {
            if (!scriptFile.delete()) LOG.warn("Could not delete script file: " + scriptFile.getAbsolutePath());
        }
        return results;
    }

    @Nullable
    private static List<String> populateDebugCommandLine(Project project, int debugPort) {
        List<String> command = new ArrayList<>();
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        String versionString = null;
        String homePath = null;
        if (sdk != null) {
            versionString = sdk.getVersionString();
            homePath = sdk.getHomePath();
        } else {
            RakuSDKService backupSDKService = project.getService(RakuSDKService.class);
            String backupSDKPath = backupSDKService.getProjectSdkPath();
            if (backupSDKPath != null) {
                versionString = RakuSdkType.getInstance().getVersionString(backupSDKPath);
                homePath = backupSDKPath;
            }
        }

        if (versionString == null || homePath == null) {
            return null;
        }

        if (VersionComparatorUtil.compare(versionString, "v2019.07") >= 0) {
            String rakuBinary = RakuSdkType.findRakuInSdkHome(homePath);
            if (rakuBinary == null) return null;
            command.add(rakuBinary);
            command.add("--debug-port=" + debugPort);
            command.add("--debug-suspend");
        } else {
            Map<String, String> moarBuildConfiguration = RakuSdkType.getInstance().getMoarBuildConfiguration(project);
            if (moarBuildConfiguration == null) {
                return null;
            }
            String prefix = moarBuildConfiguration.getOrDefault("raku::prefix", null);
            if (prefix == null) {
                prefix = moarBuildConfiguration.getOrDefault("Raku::prefix", "");
            }
            command.add(Paths.get(prefix, "bin", "moar").toString());
            // Always start suspended so we have time to send breakpoints and event handlers.
            // If the option is disabled, we'll resume right after that.
            command.add("--debug-port=" + debugPort);
            command.add("--debug-suspend");
            command.add("--libpath=" + Paths.get(prefix, "share", "nqp", "lib"));
            command.add("--libpath=" + Paths.get(prefix, "share", "raku", "lib"));
            command.add("--libpath=" + Paths.get(prefix, "share", "raku", "runtime"));
            command.add(Paths.get(prefix, "share", "raku", "runtime", "raku.moarvm").toString());
        }
        return command;
    }
}
