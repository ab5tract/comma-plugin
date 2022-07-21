package edument.perl6idea.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import edument.perl6idea.cro.run.Perl6CroRunConfigurationBase;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.utils.Perl6CommandLine;
import edument.perl6idea.utils.Perl6ScriptRunner;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Perl6RunCommandLineState extends CommandLineState {
    protected List<String> command = new LinkedList<>();
    protected @NotNull RunProfile runConfiguration;
    protected boolean isDebug = false;

    public Perl6RunCommandLineState(ExecutionEnvironment environment) {
        super(environment);
        runConfiguration = getEnvironment().getRunProfile();
    }

    protected void populateRunCommand() throws ExecutionException {
        setInterpreterParameters();
    }

    protected void setInterpreterParameters() {
        String params = ((Perl6RunConfiguration)runConfiguration).getInterpreterParameters();
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
            if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win"))
                cmd = new Perl6CommandLine(getEnvironment().getProject(), ((Perl6RunConfiguration)runConfiguration).getDebugPort());
            else
                cmd = new Perl6ScriptRunner(getEnvironment().getProject(), ((Perl6RunConfiguration)runConfiguration).getDebugPort());
        } else {
            if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win"))
                cmd = new Perl6CommandLine(getEnvironment().getProject());
            else
                cmd = new Perl6ScriptRunner(getEnvironment().getProject());
        }
        cmd.setWorkDirectory(((Perl6RunConfiguration)runConfiguration).getWorkingDirectory());
        cmd.addParameters(command);
        setEnvironment(cmd);
        KillableColoredProcessHandler handler = new KillableColoredProcessHandler(cmd);
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
        cmd.withEnvironment(((Perl6RunConfiguration)runConfiguration).getEnvs());
        if (runConfiguration instanceof Perl6CroRunConfigurationBase &&
            ((Perl6CroRunConfigurationBase)runConfiguration).getCroDevMode()) {
            cmd.withEnvironment("CRO_DEV", "1");
        }
    }

    protected void setListeners(KillableColoredProcessHandler handler) {}

    private void setScript() {
        command.add(((Perl6RunConfiguration)runConfiguration).getScriptPath());
        String params = ((Perl6RunConfiguration)runConfiguration).getProgramParameters();
        // To avoid a call like `perl6 script.p6 ""`
        if (params != null && !params.trim().isEmpty())
            command.addAll(Arrays.asList(params.split(" ")));
    }
}
