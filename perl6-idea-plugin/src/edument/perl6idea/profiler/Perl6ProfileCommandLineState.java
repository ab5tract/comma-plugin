package edument.perl6idea.profiler;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import edument.perl6idea.run.Perl6RunCommandLineState;
import edument.perl6idea.sdk.Perl6SdkType;

import java.io.File;
import java.io.IOException;

public class Perl6ProfileCommandLineState extends Perl6RunCommandLineState {
    static Logger LOG = Logger.getInstance(Perl6ProfileCommandLineState.class);
    private File tempFile = null;

    public Perl6ProfileCommandLineState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    protected void populateRunCommand() throws ExecutionException {
        checkSDK();
        String canonicalPath = null;
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
