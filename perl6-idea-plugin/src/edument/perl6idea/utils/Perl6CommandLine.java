package edument.perl6idea.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
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
 * Warning: Perl6CommandLine usage is *synchronous*. It means that it will block
 * for scripts that take a lot of time to execute and setting execution
 * into separate thread is on the caller side.
 */
public class Perl6CommandLine extends PtyCommandLine {
    private static Logger LOG = Logger.getInstance(Perl6CommandLine.class);

    public Perl6CommandLine(Sdk sdk) throws ExecutionException {
        this(sdk.getHomePath());
    }

    public Perl6CommandLine(Project project) throws ExecutionException {
        this(Perl6SdkType.getSdkHomeByProject(project));
    }

    protected Perl6CommandLine(@Nullable String sdkHome) throws ExecutionException {
        if (sdkHome == null)
            throw new ExecutionException("No SDK for project");
        if (Paths.get(sdkHome).toFile().isFile())
            setExePath(sdkHome);
        else {
            String perl6Binary = Perl6SdkType.findPerl6InSdkHome(sdkHome);
            if (perl6Binary == null)
                throw new ExecutionException("SDK is invalid");
            setExePath(perl6Binary);
        }
    }

    public Perl6CommandLine(Project project, int debugPort) throws ExecutionException {
        List<String> parameters = populateDebugCommandLine(project, debugPort);
        if (parameters == null)
            throw new ExecutionException("SDK is not valid for debugging");
        setExePath(parameters.get(0));
        addParameters(parameters.subList(1, parameters.size()));
    }

    @NotNull
    public List<String> executeAndRead() {
        List<String> results = new LinkedList<>();
        try {
            Process p = createProcess();
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))
            ) {
                String line;
                while ((line = reader.readLine()) != null)
                    results.add(line);
                if (p.waitFor() != 0)
                    return new ArrayList<>();
            } catch (IOException e) {
                LOG.warn(e);
            }
        } catch (InterruptedException | ExecutionException e) {
            LOG.warn(e);
        }
        return results;
    }

    @Nullable
    private static List<String> populateDebugCommandLine(Project project, int debugPort) {
        List<String> command = new ArrayList<>();
        Perl6SdkType projectSdk = Perl6SdkType.getInstance();
        Map<String, String> moarBuildConfiguration = projectSdk.getMoarBuildConfiguration(project);
        if (moarBuildConfiguration == null) {
            return null;
        }
        String prefix = moarBuildConfiguration.getOrDefault("perl6::prefix", "");
        command.add(Paths.get(prefix, "bin", "moar").toString());
        // Always start suspended so we have time to send breakpoints and event handlers.
        // If the option is disabled, we'll resume right after that.
        command.add("--debug-port=" + debugPort);
        command.add("--debug-suspend");
        command.add("--libpath=" + Paths.get(prefix, "share", "nqp", "lib"));
        command.add("--libpath=" + Paths.get(prefix, "share", "perl6", "lib"));
        command.add("--libpath=" + Paths.get(prefix,"share", "perl6", "runtime"));
        command.add(Paths.get(prefix, "share", "perl6", "runtime", "perl6.moarvm").toString());
        return command;
    }
}
