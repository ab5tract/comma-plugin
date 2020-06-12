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
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.util.Disposer;
import edument.perl6idea.run.Perl6DebuggableConfiguration;
import edument.perl6idea.utils.Perl6ScriptRunner;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Perl6TestRunningState extends CommandLineState {
    private final Perl6TestRunConfiguration runConfiguration;
    boolean isDebugging;

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
        final Perl6TestRunConfiguration runConfiguration = (Perl6TestRunConfiguration)env.getRunProfile();
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
        Perl6ScriptRunner cmd;

        if (isDebugging) {
            Perl6DebuggableConfiguration runConf = ((Perl6DebuggableConfiguration)getEnvironment().getRunProfile());
            cmd = new Perl6ScriptRunner(project, runConf.getDebugPort());
        }
        else {
            cmd = new Perl6ScriptRunner(project);
        }
        File script = Perl6Utils.getResourceAsFile("testing/perl6-test-harness.p6");
        if (script == null)
            throw new ExecutionException("Bundled resources are corrupted");

        cmd.addParameter(script.getAbsolutePath());

        switch (runConfiguration.getTestKind()) {
            case ALL: {
                Predicate<Module> modulesPredicate = m -> true;
                populateTestDirectoriesByModules(project, cmd, modulesPredicate);
                break;
            }
            case MODULE: {
                Predicate<Module> modulesPredicate = m -> m.getName().equals(runConfiguration.getModuleName());
                List<Module> modules = populateTestDirectoriesByModules(project, cmd, modulesPredicate);
                if (modules.size() == 0)
                    throw new ExecutionException(String.format("No module with name %s to run its tests", runConfiguration.getModuleName()));
                break;
            }
            case DIRECTORY: {
                cmd.addParameter("--paths=" + runConfiguration.getDirectoryPath());
                break;
            }
            case PATTERN: {
                Predicate<Module> modulesPredicate = m -> true;
                populateTestDirectoriesByModules(project, cmd, modulesPredicate);
                cmd.addParameter("--pattern=" + runConfiguration.getFilePattern());
                break;
            }
            case FILE: {
                cmd.addParameter("--paths=" + runConfiguration.getFilePath());
                break;
            }
        }
        if (!runConfiguration.getInterpreterParameters().isEmpty()) {
            for (String arg : runConfiguration.getInterpreterParameters().split("\\s+")) {
                cmd.addParameter("--args=" + arg);
            }
        }
        cmd.setWorkDirectory(project.getBasePath());

        cmd.withEnvironment("TEST_JOBS", String.valueOf(runConfiguration.getParallelismDegree()));
        cmd.withEnvironment(runConfiguration.getEnvs());
        cmd.withParentEnvironmentType(runConfiguration.isPassParentEnvs()
                                      ? GeneralCommandLine.ParentEnvironmentType.SYSTEM
                                      : GeneralCommandLine.ParentEnvironmentType.NONE);
        return cmd;
    }

    @NotNull
    private static List<Module> populateTestDirectoriesByModules(Project project, Perl6ScriptRunner cmd, Predicate<Module> modulesPredicate) {
        List<Module> modules = Arrays.stream(ModuleManager.getInstance(project).getModules())
            .filter(modulesPredicate).collect(Collectors.toList());
        for (Module module : modules) {
            ContentEntry[] contentEntries = ModuleRootManager.getInstance(module).getContentEntries();
            for (ContentEntry entry : contentEntries) {
                for (SourceFolder folder : entry.getSourceFolders()) {
                    if (folder.isTestSource() && folder.getFile() != null) {
                        cmd.addParameter("--paths=" + folder.getFile().getCanonicalPath());
                    }
                }
            }
        }
        return modules;
    }

    static class Perl6TestConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {
        Perl6TestConsoleProperties(RunConfiguration runConfiguration, ExecutionEnvironment env) {
            super(runConfiguration, "PERL6_TEST_CONFIGURATION", env.getExecutor());
        }

        @Override
        public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName,
                                                                            @NotNull TestConsoleProperties consoleProperties) {
            return new TapOutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties, getProject().getBaseDir().getUrl());
        }
    }
}
