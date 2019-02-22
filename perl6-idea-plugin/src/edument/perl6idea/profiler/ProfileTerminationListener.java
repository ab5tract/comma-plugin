package edument.perl6idea.profiler;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ProfileTerminationListener extends ProcessAdapter {
    public static final Logger LOG = Logger.getInstance(ProfileTerminationListener.class);
    private final File file;
    private final Project project;
    private Exception myException = null;

    ProfileTerminationListener(File file, Project project) {
        this.file = file;
        this.project = project;
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        if (event.getExitCode() != 0) {
            file.delete();
            LOG.warn(new ExecutionException("The executed process has finished with an error, cannot show the profiling data."));
        } else {
            ProgressManager.getInstance().run(new Perl6ProfileTask(project, "Processing Profiling Data", true, file));
        }
    }

    public Exception getException() {
        return myException;
    }
}
