package edument.perl6idea.profiler;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.wm.ToolWindowManager;
import edument.perl6idea.run.Perl6RunCommandLineState;
import edument.perl6idea.sdk.Perl6SdkType;

import java.io.File;
import java.io.IOException;

public class Perl6ProfileCommandLineState extends Perl6RunCommandLineState {
    static Logger LOG = Logger.getInstance(Perl6ProfileCommandLineState.class);
    private File tempFile = null;

    public Perl6ProfileCommandLineState(ExecutionEnvironment environment) {
        super(environment);
        // Un-register previously created tool window to avoid double registering
        ApplicationManager.getApplication().invokeLater(() -> {
            ToolWindowManager.getInstance(environment.getProject()).unregisterToolWindow("Perl 6 profiling tool window");
        });
    }

    @Override
    protected void populateRunCommand() throws ExecutionException {
        checkSDK();
        String canonicalPath;
        try {
            tempFile = FileUtil.createTempFile("comma-profiler", ".sql");
            // We use safe canonical path here, as running profiler is not performance-critical operation
            canonicalPath = tempFile.getCanonicalPath();
        }
        catch (IOException e) {
            LOG.warn(e);
            throw new ExecutionException(e.getMessage());
        }
        command.add(Perl6SdkType.perl6Command());
        command.add("--profile");
        command.add(String.format("--profile-filename=%s", canonicalPath));
        setInterpreterParameters();
    }

    @Override
    protected void setListeners(KillableColoredProcessHandler handler) {

        handler.addProcessListener(new ProfileTerminationListener(tempFile, getEnvironment().getProject()));
    }
}
