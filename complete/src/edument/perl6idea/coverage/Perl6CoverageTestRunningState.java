package edument.perl6idea.coverage;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.util.io.FileUtil;
import edument.perl6idea.testing.Perl6TestRunningState;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class Perl6CoverageTestRunningState extends Perl6TestRunningState {
    private File coverageDir;

    public Perl6CoverageTestRunningState(ExecutionEnvironment environment) throws ExecutionException {
        super(environment);
        try {
          coverageDir = FileUtil.createTempDirectory("coverage", Integer.toString(this.hashCode()));
          coverageDir.deleteOnExit();
        }
        catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    protected GeneralCommandLine createCommandLine() throws ExecutionException {
      GeneralCommandLine cmd = super.createCommandLine();
      cmd.withEnvironment("COMMA_TEST_COVERAGE", coverageDir.getAbsolutePath());
      return cmd;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        Perl6CoverageTestRunningState state = this;
        ProcessHandler handler = super.startProcess();
        handler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                File index = new File(coverageDir.getAbsolutePath() + File.separator + "index");
                if (index.exists()) {
                    Perl6CoverageDataManager.getInstance(getEnvironment().getProject())
                            .addSuiteFromIndexFile(index, state);
                }
                else {
                    Notifications.Bus.notify(new Notification("Coverage Error", "Coverage Error",
                            "No coverage data collected.", NotificationType.ERROR));
                }
            }
        });
        return handler;
    }
}
