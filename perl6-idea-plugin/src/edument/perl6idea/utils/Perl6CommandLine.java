package edument.perl6idea.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import edument.perl6idea.run.Perl6DebuggableConfiguration;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Perl6CommandLine {
    private static Logger LOG = Logger.getInstance(Perl6CommandLine.class);

    @NotNull
    public static GeneralCommandLine getCustomPerl6CommandLine(List<String> command, String workingPath) {
        GeneralCommandLine line = command == null ? new GeneralCommandLine() : new GeneralCommandLine(command);
        return line
          .withWorkDirectory(workingPath)
          .withCharset(CharsetToolkit.UTF8_CHARSET);
    }

    @NotNull
    public static GeneralCommandLine getCustomPerl6CommandLine(String command, String workingPath) {
        GeneralCommandLine line = command == null ? new GeneralCommandLine() : new GeneralCommandLine(command);
        return line
          .withWorkDirectory(workingPath)
          .withCharset(CharsetToolkit.UTF8_CHARSET);
    }

    @NotNull
    public static GeneralCommandLine getPerl6CommandLine(String workingPath, String homePath) {
        return getCustomPerl6CommandLine((String)null, workingPath)
          .withExePath(Paths.get(homePath, Perl6SdkType.perl6Command()).toString());
    }

    public static GeneralCommandLine pushFile(GeneralCommandLine cmd, File script) throws ExecutionException {
        try {
            // We pass -Ilib after script path, because it is the script argument
            cmd.addParameters(script.getCanonicalPath(), "-Ilib");
            return cmd;
        } catch (IOException e) {
            LOG.error(e);
            throw new ExecutionException("Could not get execution script");
        }
    }

    public static List<String> execute(GeneralCommandLine cmd) {
        List<String> results = new ArrayList<>();
        AtomicBoolean died = new AtomicBoolean(false);
        Semaphore readerDone = new Semaphore(0);
        try {
            Process p = cmd.createProcess();
            new Thread(() -> readFromProcess(results, died, p, readerDone)).start();
            readerDone.acquire();
            p.waitFor();
            if (died.get()) return null;
        } catch (InterruptedException | ExecutionException e) {
            LOG.warn(e);
            return null;
        }
        return results;
    }

    private static void readFromProcess(List<String> results, AtomicBoolean died, Process p, Semaphore readerDone) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
        try {
            String result;
            while ((result = reader.readLine()) != null)
                results.add(result);
        } catch (IOException e) {
            died.set(true);
            LOG.error(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                died.set(true);
                LOG.error(e);
            }
            readerDone.release();
        }
    }

    public static List<String> populateDebugCommandLine(Project project, Perl6DebuggableConfiguration runConfiguration) {
        List<String> command = new ArrayList<>();
        Perl6SdkType projectSdk = Perl6SdkType.getInstance();
        Map<String, String> moarBuildConfiguration = projectSdk.getMoarBuildConfiguration(project);
        if (moarBuildConfiguration == null) {
            return null;
        }
        String prefix = moarBuildConfiguration.getOrDefault("perl6::prefix", "");
        command.add(Paths.get(prefix, "bin", "moar").toString());
        command.add("--debug-port=" + runConfiguration.getDebugPort());
        if (runConfiguration.isStartSuspended())
            command.add("--debug-suspend");
        command.add("--libpath=" + Paths.get(prefix, "share", "nqp", "lib"));
        command.add("--libpath=" + Paths.get(prefix, "share", "perl6", "lib"));
        command.add("--libpath=" + Paths.get(prefix,"share", "perl6", "runtime"));
        command.add(Paths.get(prefix, "share", "perl6", "runtime", "perl6.moarvm").toString());
        return command;
    }
}
