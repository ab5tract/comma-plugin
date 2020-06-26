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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.run.Perl6DebuggableConfiguration;
import edument.perl6idea.utils.Perl6ScriptRunner;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
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

        fillTestHarnessArguments(project, cmd);
        if (!cmd.getParametersList().getParametersString().contains("--path"))
            throw new ExecutionException("No test source roots in the project: is it properly configured?");
        if (!runConfiguration.getTestPattern().isEmpty())
            cmd.addParameter("--pattern=" + runConfiguration.getTestPattern());
        cmd.setWorkDirectory(project.getBasePath());

        cmd.withEnvironment("TEST_JOBS", String.valueOf(runConfiguration.getParallelismDegree()));
        cmd.withEnvironment(runConfiguration.getEnvs());
        cmd.withParentEnvironmentType(runConfiguration.isPassParentEnvs()
                                      ? GeneralCommandLine.ParentEnvironmentType.SYSTEM
                                      : GeneralCommandLine.ParentEnvironmentType.NONE);
        return cmd;
    }

    private void fillTestHarnessArguments(Project project, Perl6ScriptRunner cmd) throws ExecutionException {
        // Depending on the target, we have to fill one or more data points
        // consisting of the path to test, current working directory and arguments
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
                    throw new ExecutionException(
                        String.format("No module with name %s to run its tests", runConfiguration.getModuleName()));
                break;
            }
            case DIRECTORY:
            case FILE: {
                VirtualFile fileToTest = LocalFileSystem.getInstance().findFileByPath(
                    runConfiguration.getTestKind() == RakuTestKind.DIRECTORY
                    ? runConfiguration.getDirectoryPath()
                    : runConfiguration.getFilePath()
                );
                if (fileToTest == null) return;
                Module module = ModuleUtilCore.findModuleForFile(fileToTest, project);
                if (module == null) return;
                ContentEntry[] contentEntries = ModuleRootManager.getInstance(module).getContentEntries();
                addParametersForContentEntry(module, cmd, fileToTest, contentEntries);
                break;
            }
        }
    }

    @NotNull
    private List<Module> populateTestDirectoriesByModules(Project project, Perl6ScriptRunner cmd,
                                                          Predicate<Module> modulesPredicate) {
        List<Module> modules = Arrays.stream(ModuleManager.getInstance(project).getModules())
            .filter(modulesPredicate).collect(Collectors.toList());
        for (Module module : modules) {
            ContentEntry[] contentEntries = ModuleRootManager.getInstance(module).getContentEntries();
            if (contentEntries.length != 1) {
                Logger.getInstance(Perl6TestRunningState.class).warn("Abundant content entries for module " + module.getName());
                continue;
            }
            for (SourceFolder folder : contentEntries[0].getSourceFolders()) {
                if (folder.isTestSource() && folder.getFile() != null) {
                    addParametersForContentEntry(module, cmd, folder.getFile(), contentEntries);
                }
            }
        }
        return modules;
    }

    private void addParametersForContentEntry(Module module, Perl6ScriptRunner cmd, VirtualFile virtualFileToTest, ContentEntry[] contentEntries) {
        if (contentEntries.length != 1) {
            Logger.getInstance(Perl6TestRunningState.class).warn("Abundant content entries for module " + module.getName());
            return;
        }

        VirtualFile rakuModuleRoot = contentEntries[0].getFile();
        if (rakuModuleRoot == null) {
            Logger.getInstance(Perl6TestRunningState.class).warn("Content entry without a root for " + module.getName());
            return;
        }

        cmd.addParameter("--paths=" + virtualFileToTest.getCanonicalPath());
        cmd.addParameter("--cwd=" + rakuModuleRoot.getPath());
        cmd.addParameter("--args=" + runConfiguration.getInterpreterArguments());
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
