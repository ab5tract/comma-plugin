package org.raku.testing;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.process.*;
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
import org.raku.utils.RakuCommandLine;
import org.raku.utils.RakuScriptRunner;
import org.raku.utils.RakuUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RakuTestRunningState extends CommandLineState {
    private final RakuTestRunConfiguration runConfiguration;
    private final Project myProject;
    private File myScriptFile;

    public RakuTestRunningState(ExecutionEnvironment environment) {
        super(environment);
        runConfiguration = (RakuTestRunConfiguration)getEnvironment().getRunProfile();
        myProject = environment.getProject();
    }

    @Override
    @NotNull
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        final ConsoleView consoleView = createConsole();
        final ProcessHandler processHandler = startProcess();
        consoleView.attachToProcess(processHandler);
        return new DefaultExecutionResult(consoleView, processHandler);
    }

    private ConsoleView createConsole() {
        final RakuTestRunConfiguration runConfiguration = (RakuTestRunConfiguration)getEnvironment().getRunProfile();
        final TestConsoleProperties testConsoleProperties = new Perl6TestConsoleProperties(runConfiguration, getEnvironment());
        final ConsoleView consoleView = SMTestRunnerConnectionUtil.createConsole("Raku tests", testConsoleProperties);
        Disposer.register(getEnvironment(), consoleView);
        return consoleView;
    }

    @Override
    @NotNull
    protected ProcessHandler startProcess() throws ExecutionException {
        final GeneralCommandLine commandLine = createCommandLine();
        final OSProcessHandler processHandler = new ColoredProcessHandler(commandLine);
        ProcessTerminatedListener.attach(processHandler, getEnvironment().getProject());
        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                myScriptFile.delete();
            }
        });
        return processHandler;
    }

    protected GeneralCommandLine createCommandLine() throws ExecutionException {
        GeneralCommandLine cmd;

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win"))
            cmd = new RakuCommandLine(myProject);
        else
            cmd = new RakuScriptRunner(myProject);
        myScriptFile = RakuUtils.getResourceAsFile("testing/rakuidea-test-harness.p6");
        if (myScriptFile == null)
            throw new ExecutionException("Bundled resources are corrupted");

        cmd.addParameter(myScriptFile.getAbsolutePath());

        fillTestHarnessArguments(cmd);
        if (!cmd.getParametersList().getParametersString().contains("--path"))
            throw new ExecutionException("No test source roots in the project: is it properly configured?");
        if (!runConfiguration.getTestPattern().isEmpty())
            cmd.addParameter("--pattern=" + runConfiguration.getTestPattern());
        cmd.setWorkDirectory(myProject.getBasePath());

        cmd.withEnvironment("TEST_JOBS", String.valueOf(runConfiguration.getParallelismDegree()));
        cmd.withEnvironment(runConfiguration.getEnvs());
        return cmd;
    }

    private void fillTestHarnessArguments(GeneralCommandLine cmd) throws ExecutionException {
        // Depending on the target, we have to fill one or more data points
        // consisting of the path to test, current working directory and arguments
        switch (runConfiguration.getTestKind()) {
            case ALL: {
                Predicate<Module> modulesPredicate = m -> true;
                populateTestDirectoriesByModules(myProject, cmd, modulesPredicate);
                break;
            }
            case MODULE: {
                Predicate<Module> modulesPredicate = m -> m.getName().equals(runConfiguration.getModuleName());
                List<Module> modules = populateTestDirectoriesByModules(myProject, cmd, modulesPredicate);
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
                Module module = ModuleUtilCore.findModuleForFile(fileToTest, myProject);
                if (module == null) return;
                ContentEntry[] contentEntries = ModuleRootManager.getInstance(module).getContentEntries();
                addParametersForContentEntry(module, cmd, fileToTest, contentEntries);
                break;
            }
        }
    }

    @NotNull
    private List<Module> populateTestDirectoriesByModules(Project project, GeneralCommandLine cmd,
                                                          Predicate<Module> modulesPredicate) {
        List<Module> modules = Arrays.stream(ModuleManager.getInstance(project).getModules())
            .filter(modulesPredicate).collect(Collectors.toList());
        for (Module module : modules) {
            ContentEntry[] contentEntries = ModuleRootManager.getInstance(module).getContentEntries();
            if (contentEntries.length != 1) {
                Logger.getInstance(RakuTestRunningState.class).warn("Abundant content entries for module " + module.getName());
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

    private void addParametersForContentEntry(Module module, GeneralCommandLine cmd, VirtualFile virtualFileToTest, ContentEntry[] contentEntries) {
        if (contentEntries.length != 1) {
            Logger.getInstance(RakuTestRunningState.class).warn("Abundant content entries for module " + module.getName());
            return;
        }

        VirtualFile rakuModuleRoot = contentEntries[0].getFile();
        if (rakuModuleRoot == null) {
            Logger.getInstance(RakuTestRunningState.class).warn("Content entry without a root for " + module.getName());
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
            Project project = consoleProperties.getProject();
            List<String> bases = new ArrayList<>();
            for (Module module : ModuleManager.getInstance(project).getModules()) {
                ContentEntry[] entries = ModuleRootManager.getInstance(module).getContentEntries();
                if (entries.length == 1 && entries[0].getFile() != null) {
                    bases.add(entries[0].getFile().getPath());
                }
            }
            return new TapOutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties, bases);
        }
    }
}
