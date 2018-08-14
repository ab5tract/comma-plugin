package edument.perl6idea.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.utils.Perl6CommandLine;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;

public class Perl6RunCommandLineState extends CommandLineState {
    protected List<String> command = new LinkedList<>();
    protected Perl6RunConfiguration runConfiguration;

    protected Perl6RunCommandLineState(ExecutionEnvironment environment) {
        super(environment);
        runConfiguration = (Perl6RunConfiguration)getEnvironment().getRunProfile();
    }

    protected String checkSDK() throws ExecutionException {
        Sdk projectSdk = ProjectRootManager.getInstance(getEnvironment().getProject()).getProjectSdk();
        if (projectSdk == null)
            throw new ExecutionException("Perl 6 SDK is not set for the project, please set one");
        String path = projectSdk.getHomePath();
        if (path == null)
            throw new ExecutionException("Perl 6 SDK path is likely to be corrupt");
        return path;
    }

    protected void populateRunCommand() throws ExecutionException {
        String perl6Path = checkSDK();
        command.add(Paths.get(perl6Path, Perl6SdkType.perl6Command()).toAbsolutePath().toString());
        setInterpreterParameters();
    }

    protected void setInterpreterParameters() {
        String params = runConfiguration.getInterpreterParameters();
        if (params != null && !params.trim().isEmpty())
            command.add(params);
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        populateRunCommand();
        setScript();
        GeneralCommandLine cmd = Perl6CommandLine.getCustomPerl6CommandLine(command, runConfiguration.getWorkingDirectory());
        setEnvironment(cmd);
        KillableColoredProcessHandler handler = new KillableColoredProcessHandler(cmd, true);
        ProcessTerminatedListener.attach(handler);
        return handler;
    }

    protected void setEnvironment(GeneralCommandLine cmd) {
        cmd.withEnvironment(runConfiguration.getEnvs());
    }

    private void setScript() {
        command.add(runConfiguration.getScriptPath());
        String params = runConfiguration.getProgramParameters();
        // To avoid a call like `perl6 script.p6 ""`
        if (params != null && !params.trim().isEmpty())
            command.addAll(Arrays.asList(params.split(" ")));
    }
}
