package edument.perl6idea.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.sun.javafx.PlatformUtil;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.utils.Perl6CommandLine;
import edument.perl6idea.utils.Perl6ScriptRunner;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Perl6RunCommandLineState extends CommandLineState {
    protected List<String> command = new LinkedList<>();
    protected Perl6RunConfiguration runConfiguration;
    protected boolean isDebug = false;

    public Perl6RunCommandLineState(ExecutionEnvironment environment) {
        super(environment);
        runConfiguration = (Perl6RunConfiguration)getEnvironment().getRunProfile();
    }

    protected void populateRunCommand() throws ExecutionException {
        setInterpreterParameters();
    }

    protected void setInterpreterParameters() {
        String params = runConfiguration.getInterpreterParameters();
        if (params != null && !params.trim().isEmpty())
            command.addAll(Arrays.asList(params.split(" ")));
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        // At the beginning of starting a process, we always
        // check project's SDK for being valid, so that
        // all run configurations can assume they are running with a proper SDK
        checkSdk();
        populateRunCommand();
        setScript();
        GeneralCommandLine cmd;
        if (isDebug) {
            if (PlatformUtil.isWindows())
                cmd = new Perl6CommandLine(getEnvironment().getProject(), runConfiguration.getDebugPort());
            else
                cmd = new Perl6ScriptRunner(getEnvironment().getProject(), runConfiguration.getDebugPort());
        } else {
            if (PlatformUtil.isWindows())
                cmd = new Perl6CommandLine(getEnvironment().getProject());
            else
                cmd = new Perl6ScriptRunner(getEnvironment().getProject());
        }
        cmd.setWorkDirectory(runConfiguration.getWorkingDirectory());
        cmd.addParameters(command);
        setEnvironment(cmd);
        KillableColoredProcessHandler handler = new KillableColoredProcessHandler(cmd, true);
        ProcessTerminatedListener.attach(handler, getEnvironment().getProject());
        setListeners(handler);
        return handler;
    }

    /**
     * Checks SDK of project to be valid and is executed before process is started.
     * Overload it with caution and in custom `startProcess` implementations
     * always call either parent `startProcess` or `checkSdk` method.
     */
    protected void checkSdk() throws ExecutionException {
        String path = Perl6SdkType.getSdkHomeByProject(getEnvironment().getProject());
        if (path == null)
            throw new ExecutionException("Raku SDK is not set for the project, please set one");
    }

    protected void setEnvironment(GeneralCommandLine cmd) {
        cmd.withEnvironment(runConfiguration.getEnvs());
    }

    protected void setListeners(KillableColoredProcessHandler handler) {}

    private void setScript() {
        command.add(runConfiguration.getScriptPath());
        String params = runConfiguration.getProgramParameters();
        // To avoid a call like `perl6 script.p6 ""`
        if (params != null && !params.trim().isEmpty())
            command.addAll(Arrays.asList(params.split(" ")));
    }
}
