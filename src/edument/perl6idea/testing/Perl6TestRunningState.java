package edument.perl6idea.testing;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.CharsetToolkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class Perl6TestRunningState extends CommandLineState {
    public Perl6TestRunningState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    @NotNull
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        final ConsoleView consoleView = createConsole(getEnvironment());
        final ProcessHandler processHandler = startProcess();
        consoleView.attachToProcess(processHandler);
        return new DefaultExecutionResult(consoleView, processHandler);
    }

    private static ConsoleView createConsole(@NotNull ExecutionEnvironment env) {
        final Project project = env.getProject();
        final Perl6TestRunConfiguration runConfiguration = (Perl6TestRunConfiguration) env.getRunProfile();
        final TestConsoleProperties testConsoleProperties = new Perl6TestConsoleProperties(runConfiguration, env);
        final ConsoleView consoleView = SMTestRunnerConnectionUtil.createConsole("Perl 6 tests", testConsoleProperties);
        Disposer.register(project, consoleView);
        return consoleView;
    }

    @Override
    @NotNull
    protected ProcessHandler startProcess() throws ExecutionException {
        final GeneralCommandLine commandLine = createCommandLine();
        final OSProcessHandler processHandler = new ColoredProcessHandler(commandLine);
        ProcessTerminatedListener.attach(processHandler, getEnvironment().getProject());
        return processHandler;
    }

    private GeneralCommandLine createCommandLine() throws ExecutionException {
        Project project = getEnvironment().getProject();
        if (project.getBasePath() == null) throw new ExecutionException("SDK is not set");
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (projectSdk == null) throw new ExecutionException("SDK is not set");
        String homePath = projectSdk.getHomePath();
        if (homePath == null) throw new ExecutionException("SDK is not set");
        File testHarness = getResourceAsFile("/testing/perl6-test-harness.p6");
        GeneralCommandLine commandLine;
        if (testHarness != null) {
            if (SystemInfo.isLinux) {
                commandLine = new GeneralCommandLine()
                        .withWorkDirectory(Paths.get(project.getBasePath()).toString())
                        .withExePath(Paths.get(homePath, "perl6").toString())
                        .withCharset(CharsetToolkit.UTF8_CHARSET);
                try {
                    commandLine.addParameter(testHarness.getCanonicalPath());
                    commandLine.addParameter("-Ilib");
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new ExecutionException("Could not create execution script");
                }
            } else {
                throw new ExecutionException("Only linux is supported");
            }
        } else {
            throw new ExecutionException("Could not create execution script");
        }
        return commandLine;
    }

    static class Perl6TestConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {
        public Perl6TestConsoleProperties(RunConfiguration runConfiguration, ExecutionEnvironment env) {
            super(runConfiguration, "PERL6_TEST_CONFIGURATION", env.getExecutor());
        }

        @Override
        public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
            return new TapOutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties);
        }
    }

    private File getResourceAsFile(String resourcePath) {
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (in == null)
                return null;

            File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                //copy stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}