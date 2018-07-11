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
import edument.perl6idea.run.Perl6DebuggableConfiguration;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.utils.Perl6CommandLine;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Perl6TestRunningState extends CommandLineState {
    boolean isDebugging;
    private List<String> command = new ArrayList<>();

    public Perl6TestRunningState(ExecutionEnvironment environment, boolean debug) {
        super(environment);
        isDebugging = debug;
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
        final Perl6TestRunConfiguration runConfiguration = (Perl6TestRunConfiguration) env.getRunProfile();
        final TestConsoleProperties testConsoleProperties = new Perl6TestConsoleProperties(runConfiguration, env);
        final ConsoleView consoleView = SMTestRunnerConnectionUtil.createConsole("Perl 6 tests", testConsoleProperties);
        Disposer.register(env.getProject(), consoleView);
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
        if (isDebugging) {
            command = Perl6CommandLine.populateDebugCommandLine(
              getEnvironment().getProject(),
              ((Perl6DebuggableConfiguration)getEnvironment().getRunProfile()).getDebugPort());
        } else {
            Project project = getEnvironment().getProject();
            if (project.getBasePath() == null) throw new ExecutionException("SDK is not set");
            Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
            if (projectSdk == null) throw new ExecutionException("SDK is not set");
            String homePath = projectSdk.getHomePath();
            if (homePath == null) throw new ExecutionException("SDK is not set");
            command.add(Paths.get(homePath, Perl6SdkType.perl6Command()).toString());
        }
        File script = Perl6CommandLine.getResourceAsFile(this,"testing/perl6-test-harness.p6");
        if (script == null) throw new ExecutionException("Bundled resources are corrupted");
        return Perl6CommandLine.pushFile(
          Perl6CommandLine.getCustomPerl6CommandLine(
            command,
            getEnvironment().getProject().getBasePath()),
          script);
    }

    static class Perl6TestConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {
        Perl6TestConsoleProperties(RunConfiguration runConfiguration, ExecutionEnvironment env) {
            super(runConfiguration, "PERL6_TEST_CONFIGURATION", env.getExecutor());
        }

        @Override
        public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
            return new TapOutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties);
        }
    }
}
