package edument.perl6idea.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Perl6RunCommandLineState extends CommandLineState {
    protected List<String> command;
    protected Perl6RunConfiguration runConfiguration;

    protected Perl6RunCommandLineState(ExecutionEnvironment environment) throws ExecutionException {
        super(environment);
        runConfiguration = (Perl6RunConfiguration)getEnvironment().getRunProfile();
        this.command = new LinkedList<>();
        populateRunCommand();
    }

    private void populateRunCommand() throws ExecutionException {
        Sdk projectSdk = ProjectRootManager.getInstance(getEnvironment().getProject()).getProjectSdk();
        if (projectSdk == null)
            throw new ExecutionException("Perl 6 SDK is not set for the project, please set one");
        String path = projectSdk.getHomePath();
        if (path == null)
            throw new ExecutionException("Perl 6 SDK path is likely to be corrupt");
        this.command.add(Paths.get(path, "perl6").toAbsolutePath().toString());
        String params = runConfiguration.getInterpreterParameters();
        if (params != null && !params.trim().isEmpty())
            this.command.add(params);
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        setScript();
        GeneralCommandLine cmd = new GeneralCommandLine(command);
        cmd.setWorkDirectory(runConfiguration.getWorkingDirectory());
        cmd.withEnvironment(runConfiguration.getEnvs());
        KillableColoredProcessHandler handler = new KillableColoredProcessHandler(cmd, true);
        ProcessTerminatedListener.attach(handler);
        return handler;
    }

    private void setScript() {
        this.command.add(runConfiguration.getScriptPath());
        String params = runConfiguration.getInterpreterParameters();
        // To avoid a call like `perl6 script.p6 ""`
        if (params != null && !params.trim().isEmpty())
            this.command.addAll(Arrays.asList(params.split(" ")));
    }
}
