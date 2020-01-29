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
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Perl6TestRunningState extends CommandLineState {
    private final Perl6TestRunConfiguration runConfiguration;
    boolean isDebugging;
    private List<String> command = new ArrayList<>();

    public Perl6TestRunningState(ExecutionEnvironment environment, boolean debug) {
        super(environment);
        isDebugging = debug;
        runConfiguration = (Perl6TestRunConfiguration)getEnvironment().getRunProfile();
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
        final ConsoleView consoleView = SMTestRunnerConnectionUtil.createConsole("Raku tests", testConsoleProperties);
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

    protected GeneralCommandLine createCommandLine() throws ExecutionException {
        Project project = getEnvironment().getProject();
        Perl6CommandLine cmd;

        if (isDebugging) {
            Perl6DebuggableConfiguration runConf = ((Perl6DebuggableConfiguration)getEnvironment().getRunProfile());
            cmd = new Perl6CommandLine(project, runConf.getDebugPort());
        } else {
            cmd = new Perl6CommandLine(project);
        }
        File script = Perl6Utils.getResourceAsFile("testing/perl6-test-harness.p6");
        if (script == null) throw new ExecutionException("Bundled resources are corrupted");
        cmd.setWorkDirectory(project.getBasePath());
        cmd.withEnvironment("TEST_JOBS", String.valueOf(runConfiguration.getParallelismDegree()));
        cmd.addParameter(script.getAbsolutePath());
        cmd.addParameter("-Ilib");
        cmd.withEnvironment(runConfiguration.getEnvs());
        cmd.withParentEnvironmentType(runConfiguration.isPassParentEnvs()
                                      ? GeneralCommandLine.ParentEnvironmentType.SYSTEM
                                      : GeneralCommandLine.ParentEnvironmentType.NONE);
        return cmd;
    }

    static class Perl6TestConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {
        Perl6TestConsoleProperties(RunConfiguration runConfiguration, ExecutionEnvironment env) {
            super(runConfiguration, "PERL6_TEST_CONFIGURATION", env.getExecutor());
        }

        @Override
        public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
            return new TapOutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties, getProject().getBaseDir().getUrl());
        }
    }
}
