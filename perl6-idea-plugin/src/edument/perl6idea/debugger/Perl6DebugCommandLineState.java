package edument.perl6idea.debugger;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import edument.perl6idea.run.Perl6RunCommandLineState;
import edument.perl6idea.utils.Perl6CommandLine;

public class Perl6DebugCommandLineState extends Perl6RunCommandLineState {
    public Perl6DebugCommandLineState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    protected void populateRunCommand() throws ExecutionException {
        checkSDK();
        command = Perl6CommandLine.populateDebugCommandLine(getEnvironment().getProject(), runConfiguration);
        if (command == null)
            throw new ExecutionException("Perl 6 SDK is not set for the project, please set one");
        setInterpreterParameters();
    }
}
